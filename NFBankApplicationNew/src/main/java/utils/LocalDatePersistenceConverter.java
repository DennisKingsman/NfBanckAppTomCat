package utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.LocalDate;

//todo think about making a converter time.LocalDate to sql.Date
@Converter(autoApply = true)
public class LocalDatePersistenceConverter implements AttributeConverter {

    @Override
    public Object convertToDatabaseColumn(Object o) {
        return null;
    }

    @Override
    public Object convertToEntityAttribute(Object o) {
        return null;
    }
}



