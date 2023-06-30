<template>
  <div class="q-pa-md authPage">
    <div class="q-gutter-md">
      <q-card class="authPage__cardAuth my-card fixed-center">
        <q-card-section class="authPage__cardSection">
          <img class="authPage__cardImg"
               src="https://www.kumtor.kg/wp-content/themes/kumtor_new/img/logo_new.png"
          />
          <h5 class="authPage__authTitle">
            Центр Управления Производством ТО
          </h5>
          <q-input
            class ="authPage__inputClass"
            clearable
            v-model="login"
            outlined
            type="text"
            label="Логин"
            suffix="@kumtor.kg"
            lazy-rules
            :rules="[ val => val && val.length > 0 || 'Введите свой логин']"
          />

          <q-input
            class ="authPage__inputClass"
            clearable
            v-model="password"
            outlined
            :type="isPwd ? 'password' : 'text'"
            label="Пароль"
            lazy-rules
            @keyup.enter="onBtnClick"
            :rules="[ val => val && val.length > 0 || 'Введите свой пароль']">
            <template v-slot:append>
              <q-icon
                :name="isPwd ? 'visibility' : 'visibility_off'"
                class="cursor-pointer"
                @click="isPwd = !isPwd"
              />
            </template>
          </q-input>

          <q-btn
            class="shadow-2 authPage__btnLogin"
            push
            glossy
            @click="onBtnClick"
            color="primary"
            label="Войти"
            text-color="white"
          />
        </q-card-section>
      </q-card>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { useMainStore } from 'stores/main-store'
import { Notify } from 'quasar'
import { domain, errorList } from 'stores/main-config'
export default {
  name: 'AuthorizationPage',
  setup () {
    const store = useMainStore()
    const log = localStorage.getItem('name')
    console.log('LOGIN ', log)
    return {
      store,
      ready: ref(true),
      password: ref(''),
      // password: ref('12345'),
      isPwd: ref(true),
      login: ref('')
      // login: ref('azem.kakitaeva')
    }
  },
  methods: {
    onBtnClick () {
      localStorage.setItem('key', 'true')
      this.authorization()
    },
    authorization () {
      this.store.login({
        username: this.login.includes('@' + domain) ? this.login : this.login + '@' + domain,
        password: this.password,
        grant_type: 'password',
        scope: 'openid',
        client_id: 'PC-Maintenance',
        // realm: 'Time-Sheet' // old test server
        realm: 'applications', // https new server
        client_secret: 'CzbwAIMtg3AGgR1Oafu1N30542rL7YnH' // https new server
      }).then((res) => {
        console.log('RESULT LOGIN INSIDE AUTHORIZATION ', res)
        if (res.access_token) {
          const token = `Bearer ${res.access_token}`
          localStorage.setItem('refresh_token', res.refresh_token)
          localStorage.setItem('token', token)
          localStorage.setItem('key', 'true')
          localStorage.setItem('name', this.login)
          this.$router.push({ path: '/' })
        } else {
          console.log('ERRROR authorization')
          Notify.create({
            message: errorList[401],
            position: 'top',
            color: 'orange'
          })
        }
      })
        .catch((error) => {
          console.log('ERRROR authorization', error)
          Notify.create({
            message: errorList[401],
            position: 'center',
            color: 'orange'
          })
        })
    }
  }
}
</script>
