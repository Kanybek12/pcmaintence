import axios from 'axios'
import { errorList, urlLogin, urlRefreshToken } from 'stores/main-config'
import { Notify } from 'quasar'
import router from '../router/index'
export async function refreshToken () {
  console.log('Method refreshToken')
  const bodyData = {
    grant_type: 'refresh_token',
    refresh_token: localStorage.getItem('refresh_token'),
    client_id: 'PC-Maintenance',
    // realm: 'Time-Sheet' // old test server
    realm: 'applications', // https new server
    client_secret: 'CzbwAIMtg3AGgR1Oafu1N30542rL7YnH' // https new server
  }
  console.log(bodyData)
  const res = (await axios.post(urlRefreshToken, bodyData)).data
  if (res) {
    if (res.access_token) {
      const token = `Bearer ${res.access_token}`
      console.log('Refresh Token ', token)
      localStorage.setItem('refresh_token', res.refresh_token)
      localStorage.setItem('token', token)
    } else {
      showNotification(501, 'orange')
      localStorage.clear()
      router().push('/login')
      document.location.reload()
    }
  } else {
    showNotification(501, 'orange')
    localStorage.clear()
    router().push('/login')
    document.location.reload()
  }
}

export async function getRequest (url) {
  await refreshToken()
  let result
  const headers = {
    Authorization: localStorage.getItem('token')
  }
  console.log('Headers ', headers)
  await axios.get(url, {
    headers: headers
  }).catch(async err => {
    console.log('Status Response ', err)
    showNotification(98, 'orange')
  }).then((res) => {
    if (res) {
      console.log('Result ', res)
      if (res.data.code) {
        if (res.data.code === 200) {
          console.log('ok')
        } else {
          showNotificationMsg(res.data.message, 'orange')
        }
      }
      result = res.data
    }
  }).catch((error) => {
    showNotification(98, 'orange')
    console.log('Error getDirectory', error)
  })
  return result
}
export async function postLogin (body) {
  const result = (await axios.post(urlLogin, body)).data
  if (result) {
    console.log('LOGIN RESPONSE ', result)
    return result
  } else {
    showNotification(97, 'orange')
  }
}
export async function postRequest (url, data) {
  await refreshToken()
  await axios.post(url, data, {
    headers: {
      Authorization: localStorage.getItem('token')
    }
  }).catch(async err => {
    console.log('res post', err)
    showNotification(98, 'orange')
  })
    .then(res => {
      console.log('POST RES ', res)
      if (res.data) {
        if (res.data.code) {
          if (res.data.code === 200) {
            showNotification(200, 'green')
          } else {
            showNotificationMsg(res.data.message, 'orange')
          }
        }
        console.log('res post', res)
        return res.data
      } else {
        showNotification(98, 'orange')
        console.log('error getting refresh token')
      }
    })
}

export async function putRequest (url, data) {
  await refreshToken()
  console.log('PUT REQUEST ', url)
  console.log('PUT request Body ', data)
  await axios.put(url, data, {
    headers: {
      Authorization: localStorage.getItem('token')
    }
  }).catch(async err => {
    console.log('res put', err)
    showNotification(98, 'orange')
  })
    .then(res => {
      if (res.data) {
        if (res.data.code) {
          if (res.data.code === 200) {
            showNotification(202, 'green')
          } else {
            showNotificationMsg(res.data.message, 'orange')
          }
        }
        return res.data
      } else {
        showNotification(98, 'orange')
        console.log('error getting refresh token')
      }
    })
}

export async function deleteRequest (url, data) {
  await refreshToken()
  console.log('Delete REQUEST ', url)
  console.log('Delete request Body ', data)
  await axios.delete(url, {
    headers: {
      Authorization: localStorage.getItem('token'),
      'Content-Type': 'application/json; charset=utf-8'
    },
    data: data
  }).catch(async err => {
    console.log('res delete', err)
    showNotification(98, 'orange')
  })
    .then(res => {
      console.log('Delete error inside then ', res)
      if (res.data) {
        if (res.data.code) {
          if (res.data.code === 200) {
            showNotification(201, 'green')
          } else {
            showNotificationMsg(res.data.message, 'orange')
          }
        }
        return res.data
      } else {
        showNotification(98, 'orange')
        console.log('error getting refresh token')
      }
    })
}

function showNotification (code, colorStr) {
  const errorMsg = errorList[code]
  Notify.create({
    message: errorMsg,
    position: 'center',
    color: colorStr
  })
}
function showNotificationMsg (msg, colorStr) {
  Notify.create({
    message: msg,
    position: 'center',
    color: colorStr
  })
}
