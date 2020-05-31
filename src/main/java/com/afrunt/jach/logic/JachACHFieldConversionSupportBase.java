package com.afrunt.jach.logic;

import com.afrunt.beanmetadata.FieldConversionSupportBase;
import com.afrunt.jach.annotation.ACHField;
import com.afrunt.jach.exception.ACHException;
import com.afrunt.jach.metadata.ACHBeanMetadata;
import com.afrunt.jach.metadata.ACHFieldMetadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JachACHFieldConversionSupportBase extends FieldConversionSupportBase<ACHBeanMetadata, ACHFieldMetadata> implements ACHFieldConversionSupport {

    private static ACHErrorMixIn errorMixIn = new ACHErrorMixInBase();

    @Override
    public Integer valueStringToInteger(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).intValue();
    }

    @Override
    public BigInteger valueStringToBigInteger(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).toBigInteger();
    }

    @Override
    public Long valueStringToLong(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).longValue();
    }

    @Override
    public Short valueStringToShort(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return stringToBigDecimal(value, bm, fm).shortValue();
    }

    @Override
    public Date valueStringToDate(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        if (ACHField.EMPTY_DATE_PATTERN.equals(fm.getDateFormat())) {
            throwError("Date pattern should be specified for field " + fm);
        }
        try {
            return new SimpleDateFormat(fm.getDateFormat()).parse(value);
        } catch (ParseException e) {
            throw error("Error parsing date " + value + " with pattern " + fm.getDateFormat() + " for field " + fm, e);
        }
    }

    @Override
    public BigDecimal valueStringToBigDecimal(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return moveDecimalRight(stringToBigDecimal(value, bm, fm), fm.getDigitsAfterComma());
    }

    @Override
    public BigDecimal stringToBigDecimal(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        if (!StringUtil.isNumeric(value)) {
            throwError(String.format("Cannot parse string %s to number for field %s", value.trim(), fm));
        }

        return new BigDecimal(value.trim());
    }

    @Override
    public String fieldStringToString(String value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padString(value, fm.getLength());
    }


    @Override
    public String fieldShortToString(Short value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }


    @Override
    public String fieldIntegerToString(Integer value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }

    @Override
    public String fieldLongToString(Long value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }


    @Override
    public String fieldBigIntegerToString(BigInteger value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(value, fm.getLength());
    }


    @Override
    public String fieldBigDecimalToString(BigDecimal value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return padNumber(String.valueOf(
                moveDecimalLeft(value, fm.getDigitsAfterComma())
                        .longValue()), fm.getLength());
    }


    @Override
    public String fieldDateToString(Date value, ACHBeanMetadata bm, ACHFieldMetadata fm) {
        return new SimpleDateFormat(fm.getDateFormat()).format(value);
    }


    @Override
    public BigDecimal moveDecimalLeft(BigDecimal number, int digitsAfterComma) {
        return number.multiply(BigDecimal.TEN.pow(digitsAfterComma));
    }


    @Override
    public BigDecimal moveDecimalRight(BigDecimal number, int digitsAfterComma) {
        return number.divide(BigDecimal.TEN.pow(digitsAfterComma));
    }


    @Override
    public String padString(Object value, int length) {
        return StringUtil.rightPad(value.toString(), length);
    }


    @Override
    public String padNumber(Object value, int length) {
        return StringUtil.leftPad(value.toString(), length, "0");
    }

    @Override
    public void throwError(String message) throws ACHException {
        errorMixIn.throwError(message);
    }

    @Override
    public ACHException error(String message) {
        return errorMixIn.error(message);
    }

    @Override
    public ACHException error(String message, Throwable e) {
        return errorMixIn.error(message, e);
    }
}
