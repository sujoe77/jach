/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.afrunt.jach.logic;

import com.afrunt.beanmetadata.FieldConversionSupport;
import com.afrunt.jach.metadata.ACHBeanMetadata;
import com.afrunt.jach.metadata.ACHFieldMetadata;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author Andrii Frunt
 */
@SuppressWarnings("WeakerAccess")
public interface ACHFieldConversionSupport extends FieldConversionSupport<ACHBeanMetadata, ACHFieldMetadata>, ACHErrorMixIn {
    Integer valueStringToInteger(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    BigInteger valueStringToBigInteger(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    Long valueStringToLong(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    Short valueStringToShort(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    Date valueStringToDate(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    BigDecimal valueStringToBigDecimal(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    BigDecimal stringToBigDecimal(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    String fieldStringToString(String value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    String fieldShortToString(Short value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    String fieldIntegerToString(Integer value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    String fieldLongToString(Long value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    String fieldBigIntegerToString(BigInteger value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    String fieldBigDecimalToString(BigDecimal value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    String fieldDateToString(Date value, ACHBeanMetadata bm, ACHFieldMetadata fm);

    BigDecimal moveDecimalLeft(BigDecimal number, int digitsAfterComma);

    BigDecimal moveDecimalRight(BigDecimal number, int digitsAfterComma);

    String padString(Object value, int length);

    String padNumber(Object value, int length);
}
