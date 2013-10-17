package com.porterhead.rest.user.api;

import org.junit.Before;

import javax.validation.Validation;
import javax.validation.Validator;

/**
 * @version 1.0
 * @author: Iain Porter
 * @since 08/05/2013
 */
public class ValidationTst {

    protected Validator validator;

    @Before
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

}
