package com.faforever.commons.api.elide.querybuilder;

import com.faforever.commons.api.elide.ElideEntity;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

  public List<QueryCriterion> scan(Class<? extends ElideEntity> clazz) {
    log.debug("Begin root scan of {}", clazz);
    return scan(clazz, clazz, null, false);
  }

  private List<QueryCriterion> scan(Class<? extends ElideEntity> rootClass,
                                    Class<? extends ElideEntity> scanClass,
                                    String prefix, boolean forceAdvanced) {
    log.debug("Scanning class '{}' (root class '{}') with prefix '{}', forceAdvanced: {}",
      scanClass, rootClass, prefix, forceAdvanced);

    List<QueryCriterion> criteriaSet = new ArrayList<>();

    Arrays.stream(scanClass.getAnnotationsByType(ExplicitFilterDefinition.class))
      .map(annotation -> buildFromExplicit(rootClass, prefix, annotation, forceAdvanced))
      .forEach(criteriaSet::add);

    FieldUtils.getFieldsListWithAnnotation(scanClass, FieldFilterDefinition.class).stream()
      .map(field -> buildFromField(rootClass, prefix, field, forceAdvanced))
      .forEach(criteriaSet::add);

    FieldUtils.getFieldsListWithAnnotation(scanClass, TransientFilter.class).stream()
      .map(field -> processTransientFilterField(rootClass, scanClass, prefix, field))
      .forEach(criteriaSet::addAll);

    return criteriaSet;
  }

  @SneakyThrows
  private QueryCriterion buildFromExplicit(Class<? extends ElideEntity> rootClass, String prefix, ExplicitFilterDefinition annotation, boolean forceAdvanced) {
    log.debug("Found ExplicitFilterDefinition: {}", annotation);

    return criterionClass.newInstance()
      .setRootClass(rootClass)
      .setApiName(concat(prefix, annotation.filterPath()))
      .setValueType(annotation.valueType())
      .setSupportedOperators(annotation.allowedOperators().getEnumSet())
      .setProposals(Arrays.asList(annotation.proposedValues()))
      .setAllowsOnlyProposedValues(annotation.onlyProposedValues())
      .setAdvancedFilter(forceAdvanced || annotation.advancedFilter())
      .setOrder(annotation.order());
  }

  private String concat(String present, String added) {
    if (StringUtils.isBlank(present)) {
      return added;
    } else {
      return present + "." + added;
    }
  }

  @SneakyThrows
  private QueryCriterion buildFromField(Class<? extends ElideEntity> rootClass, String prefix, Field field, boolean forceAdvanced) {
    FieldFilterDefinition definition = field.getAnnotation(FieldFilterDefinition.class);
    log.debug("Found FieldFilterDefinition on field '{}': {}", field.getName(), definition);

    return criterionClass.newInstance()
      .setRootClass(rootClass)
      .setApiName(concat(prefix, field.getName()))
      .setValueType(field.getType())
      .setSupportedOperators(definition.allowedOperators().getEnumSet())
      .setProposals(Arrays.asList(definition.proposedValues()))
      .setAllowsOnlyProposedValues(definition.onlyProposedValues())
      .setAdvancedFilter(forceAdvanced || definition.advancedFilter())
      .setOrder(definition.order());
  }

  private List<QueryCriterion> processTransientFilterField(Class<? extends ElideEntity> rootClass,
                                                           Class<? extends ElideEntity> scanClass,
                                                           String currentPrefix,
                                                           Field field) {
    TransientFilter transientFilter = field.getAnnotation(TransientFilter.class);
    log.debug("Found TransientFilter on field '{}': {}", field.getName(), transientFilter);

    Class<?> fieldClass;
    if (Collection.class.isAssignableFrom(field.getType())) {
      ParameterizedType genericType = (ParameterizedType) field.getGenericType();
      fieldClass = (Class<?>) genericType.getActualTypeArguments()[0];
      log.debug("Unpacked type '{}' from generic collection type: {}", fieldClass, genericType);
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
      transientFilter.advancedFilter()
    );
  }
}
