package com.faforever.commons.api.elide.querybuilder;

import com.faforever.commons.api.elide.ElideEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ElideEntityScanner {

  private final Class<? extends QueryCriterion> criterionClass;

  public ElideEntityScanner() {
    this(QueryCriterionImpl.class);
  }

  public ElideEntityScanner(@NotNull Class<? extends QueryCriterion> criterionClass) {
    log.debug("Creating ElideEntityScanner with criterionClass: {}", criterionClass);
    this.criterionClass = criterionClass;
  }

  public Map<String, QueryCriterion> scan(Class<? extends ElideEntity> clazz) {
    log.debug("Begin root scan of {}", clazz);
    return scan(clazz, clazz, null, true);
  }

  private Map<String, QueryCriterion> scan(Class<? extends ElideEntity> rootClass,
                                           Class<? extends ElideEntity> scanClass,
                                           String prefix, boolean recursive) {
    log.debug("Scanning class `{}` (root class `{}`) with prefix `{}`, recursive: {}",
      scanClass, rootClass, prefix, recursive);

    Map<String, QueryCriterion> criteriaMap = FieldUtils.getFieldsListWithAnnotation(scanClass, FilterDefinition.class).stream()
      .map(field -> buildFromField(rootClass, prefix, field))
      .collect(Collectors.toMap(QueryCriterion::getId, Function.identity()));

    if (recursive) {
      FieldUtils.getFieldsListWithAnnotation(scanClass, TransientFilter.class).stream()
        .map(field -> processTransientFilterField(rootClass, scanClass, prefix, field))
        .forEach(criteriaMap::putAll);
    }

    return criteriaMap;
  }

  private String concat(String present, String added) {
    if (present == null) {
      return added;
    } else {
      return present + "." + added;
    }
  }

  @SneakyThrows
  private QueryCriterion buildFromField(Class<? extends ElideEntity> rootClass, String prefix, Field field) {
    FilterDefinition definition = field.getAnnotation(FilterDefinition.class);
    log.debug("Found FilterDefinition: {}", definition);

    return criterionClass.newInstance()
      .setRootClass(rootClass)
      .setApiName(concat(prefix, field.getName()))
      .setSupportedOperators(definition.allowedOperators().getEnumSet())
      .setProposals(Arrays.asList(definition.proposedValues()))
      .setAllowsOnlyProposedValues(definition.onlyProposedValues());
  }

  private Map<String, QueryCriterion> processTransientFilterField(Class<? extends ElideEntity> rootClass,
                                                                  Class<? extends ElideEntity> scanClass,
                                                                  String currentPrefix,
                                                                  Field field) {
    TransientFilter transientFilter = field.getAnnotation(TransientFilter.class);
    log.debug("Found TransientFilter: {}", transientFilter);

    Class<?> fieldClass;
    if (Collection.class.isAssignableFrom(field.getType())) {
      ParameterizedType genericType = (ParameterizedType) field.getGenericType();
      fieldClass = (Class<?>) genericType.getActualTypeArguments()[0];
      log.debug("Unpacked type `{}` from generic collection type: {}", fieldClass, genericType);
    } else {
      fieldClass = field.getType();
    }

    if (!ElideEntity.class.isAssignableFrom(scanClass)) {
      throw new IllegalArgumentException("Class does not implement interface ElideEntity: " + scanClass.getName());
    }

    //noinspection unchecked
    return scan(
      rootClass,
      (Class<? extends ElideEntity>) fieldClass,
      concat(currentPrefix, field.getName()),
      false
    );
  }
}
