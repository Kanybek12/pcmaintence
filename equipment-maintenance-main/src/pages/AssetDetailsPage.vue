<template>
  <q-layout view="hHh lpR fFf" >
    <q-header elevated class="bg-white text-black" >
      <q-toolbar class="toolbar q-header fixed-top">
        <q-toolbar-title>
          <q-avatar>
            <q-btn flat round icon="chevron_left" color="#2F3542" @click="$router.push('/')"/>
          </q-avatar>
          <span class="header-title-text">
              Информация по бортовому № {{assetCode}}
            </span>
        </q-toolbar-title>
        <q-space/>
        <div :class="statusClass">
          {{globalStatus}}
        </div>
      </q-toolbar>
    </q-header>
    <div class="mainContainer">
      <q-table
        :rows="rows"
        :columns="columns"
        row-key="status"
        :rows-per-page-options="[5, 10]"
        style="width: 100%; margin: auto; overflow-x: hidden;"
        class="my-sticky-column-table"
        wrap-cells
        no-data-label="На складе пусто">
        <template v-slot:top>
          <q-btn v-if="globalStatus != 'Статус: Завершен'" label="Добавить" style="background-color: #7C7C7C; color: white; min-width: 150px; margin-right: 30px; margin-bottom: 20px;">
            <q-menu>
              <q-list style="min-width: 150px;">
                <q-item clickable v-for="itm in stageList" :key="itm.code" @click="onStageSelect(itm)" v-close-popup>
                  <q-item-section>
                    {{itm.name}}
                  </q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-btn>
          <q-select
            v-if="showStageSelect"
            outlined
            dense
            use-input
            options-dense
            stack-label
            label="Выберите этап"
            emit-value
            map-options
            :options="stageList"
            v-model="stageValue"
            @update:model-value="selectStage"
            options-cover
            style="min-width: 150px; margin-right: 30px; margin-bottom: 20px;"/>
          <q-space/>

        </template>
        <template v-slot:header="props">
          <q-tr :props="props">
            <q-th v-for="col in props.cols" :key="col.name" :props="props" class="table-header">
              <div v-html="col.label"></div>
            </q-th>
          </q-tr>
        </template>
        <template v-slot:body-cell-action="props">
          <q-td :props="props">
            <q-btn v-if="store.currentWrq?.statusMessage !== 'Завершен' && !props.row.cancelReason" flat round color="#7C7C7C" icon="edit" @click="onEditClick(props.row)"/>
            <q-btn v-if="store.currentWrq?.statusMessage !== 'Завершен' && !props.row.cancelReason" flat round color="#7C7C7C" icon="delete_outline" @click="onDeleteClick(props.row.stageId)"/>
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
              <q-select
                :label="selectLabel"
                transition-show="scale"
                transition-hide="scale"
                outlined
                color="orange"
                use-input
                hide-bottom-space
                lazy-rules
                :rules="[ val => !!val || 'Обязательное поле']"
                v-model="locationValue"
                :options="locationOnholdNos"
                @filter="filterLocationList"
                class="dialog-box-input-element">
                <template v-slot:option="scope">
                  <q-item v-bind="scope.itemProps" style="max-width: 500px">
                    <q-item-section style="max-width: 25%">
                      {{scope.opt.code}}
                    </q-item-section>
                    <q-item-section style="max-width: 50%">
                      {{scope.opt.name}}
                    </q-item-section>

                  </q-item>
                </template>
                <template v-slot:no-option>
                  <q-item class="noOptionStyle">
                    <q-item-section class="noOptionStyleImg">
                      <q-img src="~assets/img.png"/>
                    </q-item-section>
                    <q-item-section class="noOptionStyleTxt">
                      <div @click="onCatalogAddClicked()">Не найдено в справочнике. <br> Кликнити для добавления</div>
                    </q-item-section>
                  </q-item>
                </template>
              </q-select>
              <q-input
                v-model="dateValue"
                color="orange"
                label= "Время*"
                outlined
                hide-bottom-space
                use-input
                lazy-rules
                :rules="[ val => !!val || 'Укажите время']"
                class="dialog-box-input-element"
              >
                <template v-slot:append>
                  <q-icon name="event" class="cursor-pointer">
                    <q-popup-proxy ref="qDateProxy" cover transition-show="scale" transition-hide="scale">
                      <div style="display: flex; flex-direction: column; gap: 20px;">
                        <div class="q-gutter-md row items-start">
                          <q-date v-model="dateValue" mask="YYYY-MM-DD HH:mm"/>
                          <q-time v-model="dateValue" mask="YYYY-MM-DD HH:mm"/>
                        </div>
                        <div class="row items-center justify-end">
                          <q-btn v-close-popup label="ОК" color="orange" flat />
                        </div>
                      </div>
                    </q-popup-proxy>
                  </q-icon>
                </template>
              </q-input>
              <q-input outlined
                       v-model.number="personSizeValue"
                       min="1"
                       max="99"
                       type="number"
                       color="orange"
                       hide-bottom-space
                       use-input
                       lazy-rules
                       :rules="stageValue.code === 112 ? [] : [ val => !!val  || 'Укажите количество человек', val => parseInt(val) < 100 && parseInt(val) > 0 || 'Укажите количество человек в промежутке [1, 99]'] "
                       :label="personSizeLabel"
                       class="dialog-box-input-element"/>
            </q-card-section>
            <q-card-section style="width: 50%">
              <div class="dialog-box-input-element">
                <q-input
                  v-model="commentValue"
                  outlined
                  color="orange"
                  type="textarea"
                  label= "Комментарии"
                  counter
                  maxlength="255"
                >
                  <template v-slot:hint>
                    Количество символов:
                  </template>
                </q-input>
              </div>

            </q-card-section>
          </q-card-section>
          <q-card-actions align="right">
            <q-btn label="Отменить" outline style="color: #4f4f4f; margin: 20px;" v-close-popup @click="showStageSelect = false"/>
            <q-btn :label="createBtnLabel" style="background-color: #2F3542; color: white; margin: 20px;" v-close-popup @click="saveData" :disable="createStageBtndisabled"/>
          </q-card-actions>
        </q-card>
      </q-dialog>
      <q-dialog v-model="confirmDeleteDialog" persistent>
        <q-card style="min-width: 600px; padding: 10px;">
          <q-card-section class="row items-center">
            <div class="add-dialog-title">
              Отмена этапа
            </div>
            <div class="dialog-box-input-element" style="margin-top: 10px;">
              <q-input
                v-model="reasonToDelete"
                :rules="[ val => !!val || 'Укажите причину отмены этапа']"
                outlined
                color="orange"
                label= "Причина отмены этапа*"
                type="textarea"
                counter
                maxlength="255"
              >
                <template v-slot:hint>
                  Количество символов:
                </template>
              </q-input>
            </div>
          </q-card-section>
          <q-card-section v-if="iSingleStageLeft" class="row items-center" style="margin-top: -40px;">
            <q-avatar icon="delete" color="red" text-color="white"/>
            <span class="q-ml-sm"> В случае отмены последнего этапа заявка будет удалена</span>
          </q-card-section>
          <q-card-actions align="right">
            <q-btn outline label="Вернуться" style="background-color: white; color: #7C7C7C; margin: 20px;" color="#7C7C7C" v-close-popup/>
            <q-btn label="Удалить" :disabled="deleteButtonDisabled" @click="deleteRequest" style="background-color: #2F3542; color: white; margin: 20px;" v-close-popup/>
          </q-card-actions>
        </q-card>
      </q-dialog>

    </div>
  </q-layout>

</template>

<script>

import { useMainStore } from 'stores/main-store'

export default {
  name: 'AssetDetailsPage',
  setup () {
    const store = useMainStore()
    return {
      store
    }
  },
  data () {
    return {
      // globalStatus: null,
      showStageSelect: false,
      stageValue: null,
      locationValue: null,
      dateValue: new Date().toISOString().substring(0, 16).replace('T', ' '),
      personSizeValue: null,
      commentValue: null,
      assetCode: '',
      assetsId: null,
      selectedReqId: -1,
      showAddDialog: false,
      confirmDeleteDialog: false,
      isForEdit: false,
      rowItm: null,
      reasonToDelete: '',
      locationOnholdNos: []
    }
  },
  props: [
    'assetId',
    'id',
    'status'
  ],

  created () {
    this.assetCode = this.$route.params.assetId
    this.assetsId = this.$route.params.id
    this.store.getAssetDetailsList(this.assetsId)
    this.store.getDetailsStageList()
    this.store.getReqListWithId(this.assetsId)
  },
  methods: {
    onCatalogAddClicked () {
      const index = this.stageValue.code === 112 ? 3 : 0
      this.$router.push({ name: 'catalog', params: { catalogIndex: index } })
    },
    resetAddDialogData () {
      this.locationValue = null
      this.dateValue = new Date().toISOString().substring(0, 16).replace('T', ' ')
      this.personSizeValue = null
      this.commentValue = null
    },
    saveData () {
      const body = this.isForEdit ? {
        locationCode: this.stageValue.code !== 112 ? this.locationValue.code : '',
        onHoldCode: this.stageValue.code === 112 ? this.locationValue.code : '',
        foremanQty: this.personSizeValue,
        comment: this.commentValue,
        stageDate: this.dateValue
      } : {
        stageCode: this.stageValue.code,
        locationCode: this.stageValue.code !== 112 ? this.locationValue.code : '',
        onHoldCode: this.stageValue.code === 112 ? this.locationValue.code : '',
        foremanQty: this.personSizeValue ?? '',
        comment: this.commentValue,
        stageDate: this.dateValue
      }
      // console.log('BODY ', body)
      this.showStageSelect = false
      if (this.isForEdit) {
        this.store.updateAssetDetails(body, this.assetsId, this.selectedReqId)
      } else {
        this.store.createAssetDetails(body, this.assetsId)
      }
    },
    deleteRequest () {
      this.store.deleteAssetDetail(this.assetsId, this.selectedReqId, { cancelReason: this.reasonToDelete })
      this.reasonToDelete = ''
    },
    onAddDialogShow () {
      this.locationOnholdNos = this.locationList
      this.resetAddDialogData()
      this.showAddDialog = true
      this.isForEdit = false
    },
    onDeleteClick (id) {
      this.selectedReqId = id
      this.confirmDeleteDialog = true
    },
    onEditClick (row) {
      // console.log('ROW ', row)
      this.stageValue = { code: row.stageCode, label: row.stageMessage }
      this.locationValue = this.stageValue.code === 112 ? this.store.modifiedOnholdList.find(itm => itm.code === row.onHoldCode) : this.store.modifiedLocationList.find(itm => itm.code === row.locationCode)
      this.selectedReqId = row.stageId
      this.personSizeValue = row.foremanQty
      this.commentValue = row.comment
      this.showAddDialog = true
      this.isForEdit = true
    },

    showNotification (title) {
      this.$q.notify({
        message: title,
        position: 'center',
        color: '#E2A55C'
      })
    },
    onStageSelect (stage) {
      console.log('onStageSelect ', stage)
      this.stageValue = stage
      this.selectStage(stage)
    },
    selectStage (value) {
      if (value) {
        this.onAddDialogShow()
        // console.log('SelectedStage ', this.stageValue)
      }
    },
    filterLocationList (val, update) {
      update(() => {
        this.locationOnholdNos = val ? this.locationList.filter(itm => itm.label.toLowerCase().includes(val.toLowerCase()) || itm.code.toLowerCase().includes(val.toLowerCase())) : this.locationList
      })
    }
  },

  computed: {
    deleteButtonDisabled () {
      return this.reasonToDelete.trim().length === 0
    },
    selectLabel () {
      return this.stageValue.code === 112 ? 'Причина ожидания*' : 'Место*'
    },
    personSizeLabel () {
      return this.stageValue.code === 112 ? 'Количество человек' : 'Количество человек*'
    },
    createStageBtndisabled () {
      return this.stageValue.code === 112 ? !this.locationValue || !this.dateValue || this.dateValue.length < 16 : !this.locationValue || !this.dateValue || this.dateValue.length < 16 || !this.personSizeValue || this.personSizeValue < 1 || this.personSizeValue > 99
    },
    globalStatus () {
      return 'Статус: ' + this.store.currentWrq?.statusMessage
    },
    statusClass () {
      return this.store.currentWrq?.statusMessage.toLowerCase() === 'в работе' ? 'working-status'
        : this.store.currentWrq?.statusMessage.toLowerCase() === 'завершен' ? 'completing-status'
          : 'waiting-status'
    },
    createReqDialogTitle () {
      return this.stageValue.label
    },
    createBtnLabel () {
      return this.isForEdit ? 'Сохранить' : 'Добавить'
    },
    rows () {
      return this.store.assetDetailsList
    },
    isOnholdExist () {
      return this.store.assetDetailsList.find(itm => itm.stageMessage.toLowerCase() === 'ожидание')
    },
    isCancelExist () {
      return this.store.assetDetailsList.find(itm => itm.cancelReason !== null)
    },
    iSingleStageLeft () {
      return this.store.assetDetailsList.filter(itm => itm.cancelReason === null).length === 1
    },
    locationList () {
      return this.stageValue.code === 112 ? this.store.modifiedOnholdList.filter(itm => itm.isActive === 'Y') : this.store.modifiedLocationList.filter(itm => itm.isActive === 'Y')
    },
    stageList () {
      return this.store.modifiedDetailsStageList
    },
    columns () {
      return this.isOnholdExist ? this.store.currentWrq?.statusMessage !== 'Завершен' ? this.isCancelExist ? [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'reasonOnhold',
          required: true,
          label: 'Причина ожидания',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.onHoldName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 90px; ',
          field: row => row.history
        },
        {
          name: 'cancelReason',
          required: true,
          label: 'Причина отмены',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.cancelReason
        },
        {
          name: 'action',
          required: true,
          label: 'Действия',
          align: 'center',
          style: 'min-width: 35px'
        }
      ] : [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'reasonOnhold',
          required: true,
          label: 'Причина ожидания',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.onHoldName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 90px; ',
          field: row => row.history
        },
        {
          name: 'action',
          required: true,
          label: 'Действия',
          align: 'center',
          style: 'min-width: 35px'
        }
      ] : this.isCancelExist ? [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'reasonOnhold',
          required: true,
          label: 'Причина ожидания',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.onHoldName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.history
        },
        {
          name: 'cancelReason',
          required: true,
          label: 'Причина отмены',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.cancelReason
        }
      ] : [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'reasonOnhold',
          required: true,
          label: 'Причина ожидания',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.onHoldName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 90px; ',
          field: row => row.history
        }
      ] : this.store.currentWrq?.statusMessage !== 'Завершен' ? this.isCancelExist ? [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.history
        },
        {
          name: 'cancelReason',
          required: true,
          label: 'Причина отмены',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.cancelReason
        },
        {
          name: 'action',
          required: true,
          label: 'Действия',
          align: 'center',
          style: 'min-width: 35px'
        }
      ] : [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.history
        },
        {
          name: 'action',
          required: true,
          label: 'Действия',
          align: 'center',
          style: 'min-width: 35px'
        }
      ] : this.isCancelExist ? [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.history
        },
        {
          name: 'cancelReason',
          required: true,
          label: 'Причина отмены',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.cancelReason
        }
      ] : [
        {
          name: 'status',
          required: true,
          label: 'Детали',
          align: 'center',
          style: 'min-width: 15px;' +
            'overflow-wrap: break-word;' +
            'text-overflow: ellipsis;' +
            'background-color: #EBEBEB;' +
            'color: #404040;' +
            'font-size: 14px; font-weight: 600;' +
            'box-sizing: border-box; border-bottom: 1px solid #FFFFFF; box-shadow: inset 0px 1px 0px #E6E7E9;',
          field: row => row.stageMessage
        },
        {
          name: 'place',
          required: true,
          label: 'Место',
          align: 'center',
          style: 'min-width: 15px; max-width: 250px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.locationName
        },
        {
          name: 'time',
          required: true,
          label: 'Время',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.stageDate
        },
        {
          name: 'personSize',
          required: true,
          label: 'Количество человек',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.foremanQty
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.comment
        },
        {
          name: 'history',
          required: true,
          label: 'История',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.history
        }
      ]
    }
  }
}
</script>

<style scoped lang="scss">
.table-header {
  background-color: #E2E2E2;
  color: #4F4F4F;
  font-weight: 600;
  font-size: 14px;
}
.table-column-comment {
  max-width: 100px;
  min-width: 40px;
  overflow-wrap: break-word;
  text-overflow: ellipsis
}
.mainContainer {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 30px;
  top: 80px;
  padding: 0 1%;
  @media (min-width: 1500px) {
    padding: 0 3%;
  }
}
.toolbar {
  padding: 0 1%;
  @media (min-width: 1500px) {
    padding: 0 3%;
  }
}
.working-status {
  box-sizing: border-box;
  background: #D2E4FF;
  border: 2px solid #357FEE;
  border-radius: 11px;
  font-size: 16px;
  color: #357FEE;
  padding: 5px;
}
.waiting-status {
  box-sizing: border-box;
  background: #FFF5E0;
  border: 2px solid #FFAF00;
  border-radius: 11px;
  font-size: 16px;
  color: #FFAF00;
  padding: 5px;
}
.completing-status {
  box-sizing: border-box;
  background: #F0FDF1;
  border: 2px solid #2ED13F;
  border-radius: 11px;
  font-size: 16px;
  color: #2ED13F;
  padding: 5px;
}
.buttons-row {
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.add-btn {
  position: relative;
  margin-right: 10%;
}
.add-dialog-title {
  font-family: Rubik;
  font-weight: 700;
  font-size: 26px;
  color: #2F3542;
}
.dialog-box-input-element {
  width: 95%;
  margin-bottom: 25px;
  filter: drop-shadow(0px 4px 7px rgba(139, 139, 139, 0.25));
}
.noOptionStyle{
  display: flex;
  flex-direction: column;
  min-height: 250px;
  background: #F8F8F8;
}
.noOptionStyleTxt{
  font-weight: bold;
  font-size: 9px;
  text-transform: uppercase;
  align-items: center;
  color: #2F3542;
}
.noOptionStyleTxt:nth-child(2) :hover{
  cursor: pointer;
  text-decoration: underline;
  /*font-size: 9.1px;*/
  transition: 1s;
}
</style>
