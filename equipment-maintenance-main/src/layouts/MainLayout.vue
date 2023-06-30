<template>
  <div>
    <q-layout view="hHh lpR fFf" >
      <q-header elevated class="bg-white text-black" >
        <q-toolbar class="toolbar">
          <q-toolbar-title>
            <q-avatar>
              <img src="~assets/logo_new.png">
            </q-avatar>
            <span class="header-title-text">
              Центр Управления Производством ТО
            </span>
          </q-toolbar-title>
          <q-btn flat label="Справочник" style="" @click="$router.push('/catalog')"/>
          <q-btn flat icon-right="logout" label="Выход" @click="logout"/>
        </q-toolbar>
      </q-header>
      <q-page-container>
        <RouterView />
      </q-page-container>
    </q-layout>
  </div>
</template>

<script>
import { defineComponent, ref } from 'vue'
import { useMainStore } from 'stores/main-store'
import { useI18n } from 'vue-i18n/dist/vue-i18n.esm-bundler'

export default defineComponent({
  name: 'MainLayout',
  setup () {
    const store = useMainStore()
    const { locale } = useI18n({ useScope: 'global' })
    return {
      locale,
      currentRole: ref(''),
      localeOptions: [
        { value: 'en-US', label: 'English' },
        { value: 'ru-RU', label: 'Русский' }
        // { value: 'kg-KG', label: 'Кыргызча' }
      ],
      store
    }
  },
  watch: {
    locale (val) {
      console.log('LOC', val)
      if (val === 'en-US') {
        localStorage.setItem('lang', 'en-US')
      } else {
        localStorage.setItem('lang', 'ru-RU')
      }
    }
  },
  computed: {
    name: () => localStorage.getItem('name')
  },
  methods: {
    logout () {
      localStorage.clear()
      this.$router.push('/login')
      window.location.reload()
    }
  },
  created () {
    if (this.locale === 'en-US') {
      localStorage.setItem('lang', 'en-US')
    } else {
      localStorage.setItem('lang', 'ru-RU')
    }
  }
})
</script>

<style scoped lang="scss">
.q-tabs__content .row .no-wrap .items-center .self-stretch .hide-scrollbar .relative-position .q-tabs__content--align-justify .scroll {
  display: flex;
  flex-direction: column;
}

.toolbar {
  padding: 0 1%;
  @media (min-width: 1500px) {
    padding: 0 3%;
  }
}

.header-title-text {
  font-family: Rubik, system-ui;
  font-size: 26px;
  font-style: normal;
  font-weight: 700;
  color: #2F3542;
  margin-left: 10px;

}

.mobile-app-title {
  font-size: medium;
}
</style>
