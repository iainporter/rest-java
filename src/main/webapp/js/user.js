javaRest.user = {}


/**
 * Create a user
 *
 * @param {string}
 * @param {string}
 * @param {string}
 * @param {string}
 * @param {function}
 */
javaRest.user.create = function (firstName, emailAddress, password, lastName, callback) {


  javaRest.post(
    'user',
    {user :
      {
        "firstName" : firstName,
        "emailAddress" : emailAddress
      },
    "password" : password
    },
    function (response) {
      javaRest.cookie.set('token', response.token)
      javaRest.cookie.set('userId', response.userId)
      javaRest.cookie.set('email', emailAddress)
      callback()
    },
    function(jqXHR, textStatus) {
      console.log(jqXHR)
      callback(jqXHR)
    })

}


/**
 * Get user info
 * @param {function}
 */
javaRest.user.download = function (callback) {

  javaRest.get(
    'user/' + javaRest.cookie.get('userId'),
    {},
    function (response) {
      console.log(response)
      // If the cached version is the same as the most recent
      // version, just return. Else, we will run the callback.
      if (store.get('userResponse') === JSON.stringify(response)) {
        console.log('cached')
        return false
      }


      store.set('userResponse', JSON.stringify(response))

      javaRest.user.user = response

      if (callback)
        callback()
    },
    function(jqXHR, textStatus) {
      if (callback)
        callback(jqXHR)
    })

}

/**
 * Get user info
 * @param {function}
 */
javaRest.user.get = function (callback) {


  var userResponse = store.get('userResponse')


  if (userResponse) {
    var response = JSON.parse(userResponse)
    javaRest.user.user = response.user
    // We still download the latest data in the background to make sure
    // cache is current. But we return immediately.
    javaRest.user.download(callback)
    return callback()
  }


  javaRest.user.download(callback)

}

/**
 * @return {bool}
 */
javaRest.user.is_logged_in = function () {
  return !!javaRest.cookie.get('token')
}

/**
 * Log the user in
 * @param {string}
 * @param {string}
 * @param {function} Callback. First parameter is error, if any.
 */
javaRest.user.login = function (email, password, callback) {

  javaRest.post(
    'user/login',
      {
      "username" : email,
      "password" : password
      },
    function (response) {
      javaRest.cookie.set('token', response.token)
      javaRest.cookie.set('userId', response.userId)
      javaRest.cookie.set('email', email)
      callback()

    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })

}

/**
 * Log the user in via facebook
 * @param {string}
 * @param {function} Callback. First parameter is error, if any.
 */
javaRest.user.loginSocial = function (accessToken, callback) {

  javaRest.post(
    'user/login/facebook',
      {
      "accessToken" : accessToken
      },
    function (response) {
      javaRest.cookie.set('token', response.token)
      javaRest.cookie.set('userId', response.userId)
      callback()

    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })

}


/**
 * Delete the users cookies.
 */
javaRest.user.logout = function () {
  javaRest.cookie.remove('token')
  javaRest.cookie.remove('userId')
  javaRest.cookie.remove('email')
  store.clear()
  window.location = 'index.html'
}

/**
 * Delete the users cookies.
 */
javaRest.user.reset_password = function (token, password, callback) {
  javaRest.post(
    'password/tokens/' + token,
      {
      "password" : password
      },
    function (response) {
      callback()
    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}

/**
 * Delete the users cookies.
 */
javaRest.user.send_reset_email = function (email, callback) {
  javaRest.post(
    'password/tokens',
      {
      "emailAddress" : email
      },
    function (response) {
      callback()

    },
    function(jqXHR, textStatus) {
      callback(jqXHR)
    })
}



/**
 * Update first name
 * @param {function}
 */
javaRest.user.updateName = function (value, callback) {

  javaRest.put(
    'user/' + javaRest.cookie.get('userId'),
      {
      "emailAddress" : javaRest.cookie.get('email'),
      "firstName" : value
      },
    function (response) {
      console.log(response)
      if (callback)
        callback()
      // Clear user cache
      javaRest.user.download()
    },
    function(jqXHR, textStatus) {
      if (callback)
        callback(jqXHR)
      // Clear user cache
      javaRest.user.download()
    })
}

