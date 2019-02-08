package com.faforever.commons.api.elide.querybuilder.reflection;

import com.faforever.commons.api.elide.ElideEntity;
import com.faforever.commons.api.elide.querybuilder.QueryCriterion;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ElideEntityScanner {
  public Map<String, QueryCriterion> scan(Class<? extends ElideEntity> clazz) {
    return scan(clazz, clazz, null, true);
  }

  private Map<String, QueryCriterion> scan(Class<? extends ElideEntity> rootClass,
                                           Class<? extends ElideEntity> scanClass,
                                           String prefix, boolean recursive) {
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

  private QueryCriterion buildFromField(Class<?> rootClass, String prefix, Field field) {
    FilterDefinition definition = field.getAnnotation(FilterDefinition.class);

    return new QueryCriterion(
      rootClass,
      concat(prefix, field.getName()),
      definition.allowedOperators().getEnumSet(),
      Arrays.asList(definition.proposedValues()),
      definition.onlyProposedValues()
    );
  }

  private Map<String, QueryCriterion> processTransientFilterField(Class<? extends ElideEntity> rootClass,
                                                                  Class<? extends ElideEntity> scanClass,
                                                                  String currentPrefix,
                                                                  Field field) {
    TransientFilter transientFilter = field.getAnnotation(TransientFilter.class);

    Class<?> fieldClass;
    if (Collection.class.isAssignableFrom(field.getType())) {
      ParameterizedType genericType = (ParameterizedType) field.getGenericType();
      fieldClass = (Class<?>) genericType.getActualTypeArguments()[0];
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
