package com.incept5.rest.resource;

import com.incept5.rest.user.domain.User;
import com.incept5.rest.user.domain.User;

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
