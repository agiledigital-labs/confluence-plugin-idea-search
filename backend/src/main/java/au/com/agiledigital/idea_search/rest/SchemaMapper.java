package au.com.agiledigital.idea_search.rest;

import au.com.agiledigital.idea_search.model.FedexSchema;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SchemaMapper {
  SchemaMapper MAPPER = Mappers.getMapper(SchemaMapper.class);

  void mapToSchema(FedexSchema providedMap, @MappingTarget FedexSchema targetSchema);
}