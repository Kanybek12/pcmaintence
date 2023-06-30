// import { boot } from 'quasar/wrappers'
//
// // "async" is optional;
// // more info on params: https://v2.quasar.dev/quasar-cli/boot-files
// export default boot(async (/* { app, router, ... } */) => {
//   // something to do
// })

import { boot } from 'quasar/wrappers'
import { createI18n } from 'vue-i18n'
import messages from 'src/i18n'

export default boot(({ app }) => {
  // Create I18n instance
  const i18n = createI18n({
    legacy: false,
    locale: localStorage.getItem('lang') ?? 'en-US',
    fallbackLocale: 'en-US',
    messages
  })

  // Tell app to use the I18n instance
  app.use(i18n)
})
