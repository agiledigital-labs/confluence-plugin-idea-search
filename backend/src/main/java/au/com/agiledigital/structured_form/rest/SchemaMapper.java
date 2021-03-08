package au.com.agiledigital.structured_form.rest;

import au.com.agiledigital.structured_form.model.FormSchema;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SchemaMapper {
  SchemaMapper MAPPER = Mappers.getMapper(SchemaMapper.class);

  void mapToSchema(FormSchema providedMap, @MappingTarget FormSchema targetSchema);
}
