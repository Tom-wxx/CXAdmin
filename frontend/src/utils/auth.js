import Cookies from 'js-cookie'

const SessionKey = 'Admin-Session'

export function getToken() {
  return Cookies.get(SessionKey)
}

export function setToken() {
  Cookies.set(SessionKey, '1')
}

export function removeToken() {
  Cookies.remove(SessionKey)
}
