package com.porterhead.rest.resource;

import com.porterhead.rest.user.domain.User;

/**
 * User: porter
 * Date: 08/05/2012
 * Time: 09:18
 */
public class ConsumerSimpleSecurityFilter extends SimpleSecurityFilter {

    @Override
    User getUser() {
        return BaseResourceTst.TEST_USER;
    }
}
