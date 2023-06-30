<template>
  <q-layout view="lHh lpR fFf" class="shadow-3">

    <q-drawer
      v-model="leftDrawerOpen"
      show-if-above
      elevated
      :width="300"
      :breakpoint="400"
    >
      <q-scroll-area style="height: calc(100% - 80px); margin-top: 80px; border-right: 1px solid #ddd">
        <q-list>
          <q-item v-for="(itm, index) in menuList" :key="itm.title" :id="index" active-class="my-menu-link"
                  style="padding-left: 20px;"
                  clickable
                  :active="itm.isActive"
                  @click="onMenuClick(index)"
          >
            <q-item-section style="font-size: 18px; font-weight: 600; font-style: normal; font-family: Rubik">
              {{itm.title}}
            </q-item-section>
          </q-item>
        </q-list>
      </q-scroll-area>
      <div class="absolute-top" style="height: 80px; display: flex; flex-direction: row; align-items: center">
        <q-btn flat round icon="chevron_left" color="#2F3542" @click="goToMainPage"/>
        <div class="text-weight-bold" style="font-size: 18px">Справочник </div>
      </div>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>

  </q-layout>
</template>

<script>
import { defineComponent } from 'vue'
import { useMainStore } from 'stores/main-store'

export default defineComponent({
  name: 'IndexPage',
  data () {
    return {
      menuList: [
        {
          title: 'Место',
          router: '/location',
          isActive: true
        },
        {
          title: 'Причина',
          router: '/reason',
          isActive: false
        },
        {
          title: 'Приоритет',
          router: '/priority',
          isActive: false
        },
        {
          title: 'Ожидание',
          router: '/onhold',
          isActive: false
        }
      ],
      leftDrawerOpen: false,
      selectedMenuId: 0
    }
  },
  props: [
    'catalogIndex'
  ],
  setup () {
    const store = useMainStore()
    return {
      store
    }
  },
  methods: {
    onMenuClick (index) {
      this.selectedMenuId = index
      for (let i = 0; i < this.menuList.length; i++) {
        this.menuList[i].isActive = false
      }
      this.menuList[this.selectedMenuId].isActive = true
      this.$router.push(this.menuList[index].router)
    },
    goToMainPage () {
      this.$router.push('/')
    }
  },
  created () {
    if (this.$route.params.catalogIndex) {
      console.log('Catalog Index ', this.$route.params.catalogIndex)
      this.onMenuClick(this.$route.params.catalogIndex)
    }
  }
})
</script>
<style scoped lang="scss">
.default-menu {
  background-color: white;
  color: #2F3542;
}
.my-menu-link{
  background-color: #AEAEAE;
  color: white;
}
</style>
