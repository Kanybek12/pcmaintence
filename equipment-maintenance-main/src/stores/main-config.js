
// const urlMain = 'http://test01.kumtor.kg:8088' // old test server
// const urlMain = 'http://test-web.kumtor.kg:8088' // new server
const urlMain = 'https://test-web.kumtor.kg/pcmaint_back' // https new server

// const urlLogin = 'http://kgbismanast:8085/SqlToJava_war/auth1' // old test server
// const urlRefreshToken = 'http://kgbismanast:8085/SqlToJava_war/refresh1' // old test server
const urlLogin = 'https://camp.kumtor.kg/auth_new/auth/client-secret' // https new server
const urlRefreshToken = 'https://camp.kumtor.kg/auth_new/auth/client-secret/refresh' // https new server

const domain = 'kumtor.kg' // Domain of users

const errorList = {
  97: 'Ошибка в аутентификации',
  98: 'Ошибка в сети',
  99: 'Успешный логин',
  100: 'Successful Operation',
  152: 'You have no new time sheets to approve/reject',
  153: 'Incorrect status to approve/reject was entered',
  154: 'Time sheet has already been approved',
  155: 'Time sheet has already been rejected',
  156: 'Incorrect responsibility code was entered',
  200: 'Запись добавлена',
  201: 'Успешно удалено',
  202: 'Успешно изменено',
  210: 'SQL Exception. Example: Cost center with code: 608.9 not found',
  300: 'код уже существует',
  400: 'Request body field validation example. Example: Employee code must not be null',
  401: 'Логин или пароль введен не правильно, попробуйте еще раз',
  403: 'Forbidden',
  500: 'Sorry, something went wrong',
  501: 'Время сессии истекло'
}
export { domain, errorList, urlMain, urlLogin, urlRefreshToken }
