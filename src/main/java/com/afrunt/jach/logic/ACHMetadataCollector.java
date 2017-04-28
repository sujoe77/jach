package com.afrunt.jach.logic;

import com.afrunt.beanmetadata.MetadataCollector;
import com.afrunt.jach.exception.ACHException;
import com.afrunt.jach.metadata.ACHBeanMetadata;
import com.afrunt.jach.metadata.ACHFieldMetadata;
import com.afrunt.jach.metadata.ACHMetadata;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;

/**
 * @author Andrii Frunt
 */
public class ACHMetadataCollector extends MetadataCollector<ACHMetadata, ACHBeanMetadata, ACHFieldMetadata> {

    @Override
    protected ACHMetadata newMetadata() {
        return new ACHMetadata();
    }

    @Override
    protected ACHBeanMetadata newBeanMetadata() {
        return new ACHBeanMetadata();
    }

    @Override
    protected ACHFieldMetadata newFieldMetadata() {
        return new ACHFieldMetadata();
    }

    @Override
    protected void validateFieldMetadata(ACHFieldMetadata fm) {
        super.validateFieldMetadata(fm);
        boolean achField = fm.isACHField();

        if (achField) {
            if (fm.isBlank() && fm.hasConstantValues()) {
                throw new ACHException("ACHField cannot be BLANK and contain constant values");
            }

            if (fm.isTypeTag() && (!fm.isMandatory() || fm.getConstantValues().isEmpty())) {
                throw new ACHException("TypeTag field should have some constant values and be mandatory " + fm);
            }
            validateDateField(fm);
        }
    }

    @Override
    protected boolean skipClass(Class<?> cl) {
        return super.skipClass(cl) || Modifier.isAbstract(cl.getModifiers());
    }

    private void validateDateField(ACHFieldMetadata fm) {
        if (fm.isDate()) {
            String dateFormat = fm.getDateFormat();
            if (dateFormat == null) {
                throw new ACHException("Date format is required for date fields " + fm);
            }

            try {
                new SimpleDateFormat(dateFormat);
            } catch (Exception e) {
                throw new ACHException(dateFormat + " is wrong date format for field " + fm);
            }

            if (dateFormat.length() != fm.getLength()) {
                throw new ACHException("The length of date pattern should be equal to field length");
            }
        }
    }
}
