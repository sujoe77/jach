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
package com.afrunt.jach.metadata;

import com.afrunt.beanmetadata.BeanMetadata;
import com.afrunt.jach.annotation.ACHField;
import com.afrunt.jach.annotation.ACHRecordType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.afrunt.jach.logic.StringUtil.nTimes;

/**
 * @author Andrii Frunt
 */
public class ACHBeanMetadata extends BeanMetadata<ACHFieldMetadata> {
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private List<ACHFieldMetadata> achFieldsMetadata;
    private String recordTypeCode;
    private List<ACHFieldMetadata> typeTagsMetadata;

    public String getACHRecordName() {
        return getAnnotation(ACHRecordType.class).name();
    }

    public List<ACHFieldMetadata> getACHFieldsMetadata() {
        if (achFieldsMetadata == null) {
            achFieldsMetadata = getFieldsAnnotatedWith(ACHField.class);
            Collections.sort(achFieldsMetadata);
            return achFieldsMetadata;
        }
        return achFieldsMetadata;
    }

    public List<ACHFieldMetadata> getACHTypeTagsMetadata() {
        if (typeTagsMetadata == null) {
            typeTagsMetadata = getACHFieldsMetadata();
            List<ACHFieldMetadata> ret = new ArrayList();
            for (ACHFieldMetadata meta : typeTagsMetadata) {
                if (meta.isTypeTag()) {
                    ret.add(meta);
                }
            }
            Collections.sort(typeTagsMetadata);
            typeTagsMetadata = ret;
        }
        return typeTagsMetadata;
    }

    public boolean recordTypeCodeIs(String recordTypeCode) {
        return recordTypeCode.equals(getRecordTypeCode());
    }

    @SuppressWarnings("WeakerAccess")
    public String getRecordTypeCode() {
        if (recordTypeCode == null) {
            List<String> constantValues = getFieldMetadata("recordTypeCode").getValues();
            recordTypeCode = constantValues.get(0);
        }
        return recordTypeCode;
    }

    public String getPattern() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < getACHFieldsMetadata().size(); i++) {
            sb.append(nTimes(getACHFieldsMetadata().get(i).getLength(), letter(i)));
        }
        return sb.toString();
    }

    private String letter(Integer i) {
        return String.valueOf(ALPHABET.charAt(i));
    }
}
