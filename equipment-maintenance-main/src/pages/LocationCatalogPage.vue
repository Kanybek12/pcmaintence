
<template>
  <div class="mainContainer">
    <div class="text-weight-bold" style="font-size: 20px">
      Место
    </div>
    <q-table
      :rows="rows"
      :columns="columns"
      row-key="id"
      style="width: 100%; margin: auto; overflow-x: hidden;"
      :rows-per-page-options="[10, 20, 30]"
      wrap-cells
      no-data-label="Справочник пуст">
      <template v-slot:top>
        <q-btn label="Добавить" style="background-color: #7C7C7C; color: white; min-width: 150px; margin-right: 30px; margin-bottom: 20px;" @click="onAddClick"/>
        <q-input
          label="Поиск"
          dense outlined
          v-model="searchText"
          style="min-width: 150px; margin-right: 30px; margin-bottom: 20px;">
          <template v-slot:append>
            <q-icon name="search" class="cursor-pointer" />
            <q-icon v-if="false" name="clear" class="cursor-pointer" @click="hideSearch" />
          </template>
        </q-input>
      </template>
      <template v-slot:header="props">
        <q-tr :props="props">
          <q-th v-for="col in props.cols" :key="col.name" :props="props" class="table-header">
            <div v-html="col.label"></div>
          </q-th>
        </q-tr>
      </template>
      <template v-slot:body-cell-status="props">
        <q-td :props="props">
          <div :style="props.row.isActive === 'Y' ? 'color: #2ED13F;' : 'color: #FFAF00;'">
            {{props.row.isActive === 'Y' ? 'Активен' : 'Неактивен'}}
          </div>
        </q-td>
      </template>
      <template v-slot:body-cell-action="props">
        <q-td :props="props">
          <q-btn flat round color="#2F3542" icon="edit" @click="onEditClick(props.row)"/>
        </q-td>
      </template>
    </q-table>
    <q-dialog v-model="showAddDialog">
      <q-card style="min-width: 750px; padding: 10px;">
        <q-card-section style="margin-bottom: 10px;">
          <div class="add-dialog-title">
            {{createReqDialogTitle}}
          </div>
        </q-card-section>
        <q-card-section horizontal style="width: 100%;">
          <q-card-section style="width: 50%">
            <q-input outlined
                     class="removeBottom"
                     v-model="codeStr"
                     color="orange"
                     unelevated
                     label="Код*"
                     lazy-rules
                     :rules="[(val) => (val && val.length > 0) || 'Укажите код']"
                     hide-bottom-space
                     style="width: 95%; margin-bottom: 25px; filter: drop-shadow(0px 4px 7px rgba(139, 139, 139, 0.25));"/>
            <q-select
              label="Состояние*"
              transition-show="scale"
              transition-hide="scale"
              color="orange"
              :disable="!isForEdit"
              use-input
              outlined
              v-model="selectedStatus"
              :options="statusList"
              option-value="cause"
              hide-bottom-space
              lazy-rules
              :rules="[ val => !!val || 'Выберите состояние']"
              style="width: 95%; margin-bottom: 25px; filter: drop-shadow(0px 4px 7px rgba(139, 139, 139, 0.25));"
            >
            </q-select>
          </q-card-section>
          <q-card-section style="width: 50%">
            <q-input outlined
                     v-model="descriptionStr"
                     color="orange"
                     label="Описание*"
                     hide-bottom-space
                     type="textarea"
                     counter
                     maxlength="128"
                     lazy-rules
                     :rules="[ val => !!val || 'Добавьте описание']"
                     style="width: 95%; margin-bottom: 25px; filter: drop-shadow(0px 4px 7px rgba(139, 139, 139, 0.25));">
              <template v-slot:hint>
                Количество символов:
              </template>
            </q-input>
          </q-card-section>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn label="Отменить" outline style="color: #4f4f4f; margin: 20px;" v-close-popup />
          <q-btn :label="createBtnLabel"
                 style="background-color: #2F3542; color: white; margin: 20px;"
                 v-close-popup
                 :disable="disabled"
                 @click="saveData"/>
        </q-card-actions>
      </q-card>
    </q-dialog>
    <q-dialog v-model="confirmDeleteDialog" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="delete" color="red" text-color="white"/>
          <span class="q-ml-sm"> Вы уверены удалить данный справочник?</span>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Отмена" color="blue" v-close-popup/>
          <q-btn flat label="Удалить" @click="deleteItm" color="red" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import { useMainStore } from 'stores/main-store'

export default {
  name: 'LocationCatalogPage',
  setup () {
    const store = useMainStore()
    return {
      store
    }
  },
  data () {
    return {
      searchText: '',
      showAddDialog: false,
      isForEdit: false,
      selectedOnholdId: -1,
      selectedRow: null,
      confirmDeleteDialog: false,
      codeStr: '',
      descriptionStr: '',
      statusList: [
        {
          label: 'Активен',
          value: 'Y'
        },
        {
          label: 'Неактивен',
          value: 'N'
        }
      ],
      selectedStatus: null
    }
  },
  methods: {
    saveData () {
      const body = {
        code: this.codeStr,
        name: this.descriptionStr,
        isActive: this.selectedStatus.value
      }
      if (this.isForEdit) {
        console.log('BODY PUT', body)
        this.store.updateLocationReference(body, this.selectedRow.id)
      } else {
        console.log('BODY POST', body)
        this.store.createLocationReference(body)
      }
    },
    onEditClick (row) {
      console.log('ROW ', row)
      this.codeStr = row.code
      this.selectedStatus = row.isActive === 'Y' ? { label: 'Активен', value: 'Y' } : { label: 'Неактивен', value: 'N' }
      this.descriptionStr = row.name
      this.selectedRow = row
      this.showAddDialog = true
      this.isForEdit = true
    },
    onAddClick () {
      this.resetAddDialogData()
      this.showAddDialog = true
      this.isForEdit = false
    },
    onDeleteClick (id) {
      this.selectedOnholdId = id
      this.confirmDeleteDialog = true
    },
    deleteItm () {
      this.store.deleteOnholdReference(this.selectedOnholdId)
    },
    resetAddDialogData () {
      this.codeStr = ''
      this.descriptionStr = ''
      this.selectedStatus = {
        label: 'Активен',
        value: 'Y'
      }
    }
  },
  computed: {
    disabled () {
      return !this.codeStr.trim() || !this.descriptionStr.trim() || !this.selectedStatus
    },
    createBtnLabel () {
      return this.isForEdit ? 'Сохранить' : 'Добавить'
    },
    createReqDialogTitle () {
      return this.isForEdit ? 'Редактирование Места' : 'Место'
    },
    locationData () {
      return this.store.locationList
    },
    columns () {
      return [
        {
          name: 'id',
          required: true,
          label: '№',
          align: 'center',
          style: 'min-width: 15px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.id
        },
        {
          name: 'locationCode',
          required: true,
          label: 'Код',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.code
        },
        {
          name: 'description',
          required: true,
          label: 'Описание',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.name
        },
        {
          name: 'status',
          required: true,
          label: 'Состояние',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.isActive
        },
        {
          name: 'action',
          required: true,
          label: 'Действия',
          align: 'center',
          style: 'min-width: 35px'
        }
      ]
    },
    rows () {
      this.locationData.forEach((itm, i) => {
        itm.id = i + 1
      })
      return this.locationData.filter(itm => {
        return itm.name.toLowerCase().includes(this.searchText === null ? '' : this.searchText?.toLowerCase()) ||
          itm.code.toLowerCase().includes(this.searchText === null ? '' : this.searchText?.toLowerCase())
      })
    }
  }
}
</script>

<style scoped lang="scss">
.mainContainer {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 30px;
  top: 25px;
  padding: 0 1%;
  @media (min-width: 1500px) {
    padding: 0 3%;
  }
}
.pageTitle {
  font-style: normal;
  font-family: 'Rubik';
  font-weight: 700;
  color: #2F3542;
  font-size: 26px;
}
.add-dialog-title {
  font-family: Rubik;
  font-weight: 700;
  font-size: 26px;
}
.table-header {
  background-color: #E2E2E2;
  color: #4F4F4F;
  font-weight: 600;
  font-size: 14px;
}
.q-field--error .q-field__bottom{
  background-color: transparent;
  padding-top: 5px;
}

.q-field__inner{
  box-shadow: none;
}
</style>
