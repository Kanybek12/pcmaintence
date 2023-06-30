<template>
  <div class="mainContainer">
    <q-table
      :rows="rows"
      :columns="columns"
      :rows-per-page-options="[10, 20, 30]"
      row-key="id"
      style="width: 100%; margin: auto; overflow-x: hidden;"
      v-model:pagination="pagination"
      @request="searchGetData"
      :loading="loading"
      wrap-cells
      no-data-label="На складе пусто">
      <template v-slot:top>
        <q-btn label="Добавить" style="background-color: #7C7C7C; color: white; min-width: 150px; margin-right: 30px; margin-bottom: 20px;" @click="onAddClick"/>
        <q-input v-if="false" label="Поиск" dense outlined v-model="searchText" style="min-width: 150px; margin-left: 30px; margin-bottom: 20px;">
          <template v-slot:append>
            <!-- <q-icon name="search" /> -->
            <q-icon name="search" class="cursor-pointer" />
            <q-icon v-if="false" name="clear" class="cursor-pointer" @click="hideSearch" />
          </template>
        </q-input>
        <q-space/>
        <q-btn v-if="showSearchBtn" square style="margin-bottom: 20px; background-color: #7C7C7C; color: white; margin-right: 20px;" icon="search" @click="showSearchModalView"/>
        <q-btn style="margin-bottom: 20px; background-color: #7C7C7C; color: white; " label="Скачать отчет в PowerBI" @click="goToPowerBI"/>
        <div v-if="searchModalView" style="display: block; width: 100%; padding: 10px;">
          <q-card style="width: 100%; padding-left: 20px; padding-right: 20px; filter: drop-shadow(0px 4px 7px rgba(139, 139, 139, 0.25));">
            <q-card-section >
              <div class="search-dialog-title">
                Поиск
              </div>
            </q-card-section>
            <q-card-section horizontal style="width: 100%;">
              <q-card-section style="width: 33%">
                <q-select
                  label="Бортовой номер"
                  transition-show="scale"
                  transition-hide="scale"
                  outlined
                  dense
                  options-dense
                  color="orange"
                  use-input
                  map-options
                  options-cover
                  v-model="assetNumberSearch"
                  :options="assetSearchNos"
                  @update:model-value="selectAssetNoSearch"
                  @filter="filterSearchAssetNo"
                  class="dialog-box-input-element">
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps" class="searchSelectItem">
                      <q-item-section style="width: 120px;">
                        {{scope.opt.code}}
                      </q-item-section>
                      <q-item-section style="width: 120px;">
                        {{scope.opt.number}}
                      </q-item-section>
                      <q-item-section style="width: 270px;">
                        {{scope.opt.name}}
                      </q-item-section>

                    </q-item>
                  </template>
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section>
                        Ничего не найдено
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
                <q-select
                  label="Рабочий наряд"
                  transition-show="scale"
                  transition-hide="scale"
                  color="orange"
                  use-input
                  outlined
                  dense
                  v-model="wrqNoSearch"
                  :options="wrqNosSearch"
                  option-label="code"
                  @filter="filterWrqSearchNoList"
                  class="dialog-box-input-element"
                >
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps" class="searchSelectItem">
                      <q-item-section style="width: 130px;">
                        {{scope.opt.code}}
                      </q-item-section>
                      <q-item-section style="width: 270px;">
                        {{scope.opt.name}}
                      </q-item-section>

                    </q-item>
                  </template>
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section>
                        Ничего не найдено
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
                <q-select
                  transition-show="scale"
                  transition-hide="scale"
                  color="orange"
                  use-input
                  multiple
                  outlined
                  dense
                  options-dense
                  emit-value
                  map-options
                  :options="statusListModified"
                  v-model="statusSearchValue"
                  @update:model-value="selectStatusSearch"
                  options-cover
                  label="Статус"
                  class="dialog-box-input-element"
                  model-value="">
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps" class="searchSelectItem">
                      <div class="dropdown-filter">
                        <q-checkbox :model-value="scope.opt.isChecked" color="orange" class="checkbox-not-clickable"/>
                        <div style="margin-left: 10px; width: 95%;">
                          {{scope.opt.label}}
                        </div>
                      </div>

                    </q-item>
                  </template>
                </q-select>
              </q-card-section>
              <q-card-section style="width: 33%">
                <q-select
                  transition-show="scale"
                  transition-hide="scale"
                  color="orange"
                  multiple
                  use-input
                  outlined
                  dense
                  options-dense
                  emit-value
                  map-options
                  :options="assetTypeListForSearch"
                  v-model="categorySearchValue"
                  @update:model-value="selectCategorySearch"
                  @filter="filterAssetTypeListSearch"
                  options-cover
                  label="Вид транспорта"
                  class="dialog-box-input-element"
                  model-value="">
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps" class="searchSelectItem">
                      <div class="dropdown-filter">
                        <q-checkbox :model-value="scope.opt.isChecked" color="orange" class="checkbox-not-clickable"/>
                        <div style="width: 130px;">
                          {{scope.opt.code}}
                        </div>
                        <div style="width: 270px;">
                          {{scope.opt.name}}
                        </div>
                      </div>
                    </q-item>
                  </template>
                </q-select>
                <q-select
                  label="Описание рабочего наряда"
                  transition-show="scale"
                  transition-hide="scale"
                  color="orange"
                  use-input
                  outlined
                  dense
                  v-model="wrqNoSearch"
                  :options="wrqNosSearch"
                  option-label="name"
                  @filter="filterWrqSearchDesList"
                  class="dialog-box-input-element"
                >
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps" class="searchSelectItem">
                      <q-item-section style="width: 130px;">
                        {{scope.opt.code}}
                      </q-item-section>
                      <q-item-section style="width: 270px;">
                        {{scope.opt.name}}
                      </q-item-section>

                    </q-item>
                  </template>
                  <template v-slot:no-option>
                    <q-item>
                      <q-item-section>
                        Ничего не найдено
                      </q-item-section>
                    </q-item>
                  </template>
                </q-select>
              </q-card-section>
              <q-card-section style="width: 33%">
                <q-select
                  label="Причина"
                  transition-show="scale"
                  transition-hide="scale"
                  color="orange"
                  use-input
                  dense
                  outlined
                  v-model="wrqReasonCodeSearch"
                  :options="reasonList"
                  option-value="cause"
                  class="dialog-box-input-element"
                >
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps" class="searchSelectItem">
                      <q-item-section style="max-width: 30%">
                        {{scope.opt.wrqReasonCode}}
                      </q-item-section>
                      <q-item-section style="max-width: 70%">
                        {{scope.opt.wrqReasonName}}
                      </q-item-section>

                    </q-item>
                  </template>
                </q-select>
                <q-select
                  label="Приоритет"
                  transition-show="scale"
                  transition-hide="scale"
                  color="orange"
                  use-input
                  dense
                  outlined
                  v-model="wrqPriorityCodeSearch"
                  :options="priorityList"
                  option-value="cause"
                  class="dialog-box-input-element"
                >
                  <template v-slot:option="scope">
                    <q-item v-bind="scope.itemProps" class="searchSelectItem">
                      <q-item-section style="max-width: 30%">
                        {{scope.opt.priorityCode}}
                      </q-item-section>
                      <q-item-section style="max-width: 70%">
                        {{scope.opt.priorityName}}
                      </q-item-section>

                    </q-item>
                  </template>
                </q-select>
              </q-card-section>
            </q-card-section>
            <q-card-actions align="right" style="width: 100%;">
              <q-btn label="Очистить" id="clear-btn" outline style="color: #4f4f4f; margin-right: 20px;" @click="resetSearchSelecteds()"/>
              <q-btn label="Отменить" outline style="color: #4f4f4f; marginTop: 20px; margin-bottom: 20px;" v-close-popup @click="hideSearchModalView"/>
              <q-btn label="Поиск"  style="background-color: #7C7C7C; color: white; margin-left: 20px; margin-right: 40px;" :disable="searchBtndisabled" v-close-popup @click="searchGetData({pagination: pagination})"/>
            </q-card-actions>
          </q-card>
        </div>
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
          <q-btn v-if="props.row.statusMessage !== 'Завершен'" flat class="material-icons-outlined" round color="#7C7C7C" icon="edit" @click.stop="onEditClick(props.row)"/>
          <q-btn v-if="props.row.statusMessage !== 'Завершен'" flat round color="#7C7C7C" icon="delete_outline" @click.stop="onDeleteClick(props.row.requestId)"/>
          <q-btn flat round color="#7C7C7C" icon="chevron_right" @click.stop="onRowClick(props.row)"/>
        </q-td>
      </template>
      <template v-slot:body-cell-status="props">
        <q-td :props="props">
          <div v-if="props.row.statusMessage === 'Ожидание'" style="color: #FFAF00;">
            {{props.row.statusMessage}}
          </div>
          <div v-if="props.row.statusMessage === 'В работе'" style="color: #2589FF;">
            {{props.row.statusMessage}}
          </div>
          <div v-if="props.row.statusMessage === 'Завершен'" style="color: #2ED13F;">
            {{props.row.statusMessage}}
          </div>
        </q-td>
      </template>
    </q-table>

    <q-dialog v-model="showAddDialog">
      <q-card style="min-width: 750px; padding: 20px;">
        <q-card-section style="margin-bottom: 10px;">
          <div class="add-dialog-title">
            {{createReqDialogTitle}}
          </div>
        </q-card-section>
        <q-card-section horizontal style="width: 100%;">
          <q-card-section style="width: 50%">
            <q-select
              label="Бортовой номер*"
              transition-show="scale"
              transition-hide="scale"
              outlined
              color="orange"
              hide-bottom-space
              use-input
              lazy-rules
              :rules="[ val => !!val || 'Выберите бортовой номер']"
              v-model="assetNumber"
              :options="assetNos"
              @update:model-value="selectAssetNo"
              @filter="filterAssetNo"
              option-label="code"
              class="dialog-box-input-element">
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps" style="max-width: 500px">
                  <q-item-section style="max-width: 25%">
                    {{scope.opt.code}}
                  </q-item-section>
                  <q-item-section style="max-width: 25%">
                    {{scope.opt.number}}
                  </q-item-section>
                  <q-item-section style="max-width: 50%">
                    {{scope.opt.name}}
                  </q-item-section>
                </q-item>
              </template>
              <template v-slot:no-option>
                <q-item>
                  <q-item-section>
                    Ничего не найдено
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
            <q-input disable readonly outlined v-model="assetTypeCode" color="orange" label="Вид транспорта*" class="dialog-box-input-element"/>
            <q-input
              v-model="stopDate"
              color="orange"
              label= "Дата остановки*"
              outlined
              class="dialog-box-input-element"
            >
              <template v-slot:append>
                <q-icon name="event" class="cursor-pointer">
                  <q-popup-proxy ref="qDateProxy" cover transition-show="scale" transition-hide="scale">
                    <div style="display: flex; flex-direction: column; gap: 20px;">
                      <div class="q-gutter-md row items-start">
                        <q-date v-model="stopDate" mask="YYYY-MM-DD HH:mm"/>
                        <q-time v-model="stopDate" mask="YYYY-MM-DD HH:mm"/>
                      </div>
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="OK" color="orange" flat />
                      </div>
                    </div>
                  </q-popup-proxy>
                </q-icon>
              </template>
              <template v-slot:no-option>
                <q-item>
                  <q-item-section class="noOptionStyle">
                    ничего не найдено
                  </q-item-section>
                </q-item>
              </template>
            </q-input>
            <q-select
              label="Причина*"
              transition-show="scale"
              transition-hide="scale"
              color="orange"
              use-input
              outlined
              lazy-rules
              :rules="[ val => !!val || 'Выберите причину']"
              v-model="wrqReasonCode"
              @filter="filterReasonList"
              :options="reasonNos"
              option-value="cause"
              class="dialog-box-input-element"
            >
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps" style="max-width: 400px">
                  <q-item-section style="max-width: 30%">
                    {{scope.opt.wrqReasonCode}}
                  </q-item-section>
                  <q-item-section style="max-width: 70%">
                    {{scope.opt.wrqReasonName}}
                  </q-item-section>
                </q-item>
              </template>
              <template v-slot:no-option>
                <q-item class="noOptionStyle">
                  <q-item-section class="noOptionStyleImg">
                    <q-img src="~assets/img.png"/>
                  </q-item-section>
                  <q-item-section class="noOptionStyleTxt">
                    <div @click="onCatalogAddClicked(1)">Не найдено в справочнике. <br> Кликнити для добавления</div>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
            <q-select
              label="Рабочий наряд"
              transition-show="scale"
              transition-hide="scale"
              color="orange"
              use-input
              outlined
              v-model="wrqNo"
              :options="wrqNos"
              @filter="filterWrqList"
              option-label="code"
              class="dialog-box-input-element"
            >
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps" style="max-width: 550px">
                  <q-item-section style="max-width: 20%">
                    {{scope.opt.code}}
                  </q-item-section>
                  <q-item-section style="max-width: 80%">
                    {{scope.opt.name}}
                  </q-item-section>

                </q-item>
              </template>
              <template v-slot:no-option>
                <q-item>
                  <q-item-section>
                    Ничего не найдено
                  </q-item-section>
                </q-item>
              </template>
            </q-select>

          </q-card-section>
          <q-card-section style="width: 50%">
            <q-select
              label="Описание рабочего наряда"
              transition-show="scale"
              transition-hide="scale"
              color="orange"
              use-input
              outlined
              v-model="wrqNo"
              :options="wrqNos"
              @filter="filterWrqDesList"
              option-label="name"
              class="dialog-box-input-element"
            >
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps" style="max-width: 550px">
                  <q-item-section style="max-width: 20%">
                    {{scope.opt.code}}
                  </q-item-section>
                  <q-item-section style="max-width: 80%">
                    {{scope.opt.name}}
                  </q-item-section>

                </q-item>
              </template>
              <template v-slot:no-option>
                <q-item>
                  <q-item-section>
                    Ничего не найдено
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
            <q-select
              label="Приоритет*"
              transition-show="scale"
              transition-hide="scale"
              color="orange"
              use-input
              outlined
              lazy-rules
              :rules="[ val => !!val || 'Выберите приоритет']"
              @filter="filterPriorityList"
              v-model="wrqPriorityCode"
              :options="priorityNos"
              option-value="cause"
              class="dialog-box-input-element"
            >
              <template v-slot:option="scope">
                <q-item v-bind="scope.itemProps" style="max-width: 400px">
                  <q-item-section style="max-width: 30%">
                    {{scope.opt.priorityCode}}
                  </q-item-section>
                  <q-item-section style="max-width: 70%">
                    {{scope.opt.priorityName}}
                  </q-item-section>
                </q-item>
              </template>
              <template v-slot:no-option>
                <q-item class="noOptionStyle">
                  <q-item-section class="noOptionStyleImg">
                    <q-img src="~assets/img.png"/>
                  </q-item-section>
                  <q-item-section class="noOptionStyleTxt">
                    <div @click="onCatalogAddClicked(2)">Не найдено в справочнике. <br> Кликнити для добавления</div>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
            <q-input
              outlined
              v-model="dateExpected"
              color="orange"
              label= "Ожидаемая дата запуска*"
              class="dialog-box-input-element"
            >
              <template v-slot:append>
                <q-icon name="event" class="cursor-pointer">
                  <q-popup-proxy ref="qDateProxy" cover transition-show="scale" transition-hide="scale">
                    <div style="display: flex; flex-direction: column; gap: 20px;">
                      <div class="q-gutter-md row items-start">
                        <q-date v-model="dateExpected" mask="YYYY-MM-DD HH:mm"/>
                        <q-time v-model="dateExpected" mask="YYYY-MM-DD HH:mm"/>
                      </div>
                      <div class="row items-center justify-end">
                        <q-btn v-close-popup label="OK" color="orange" flat />
                      </div>
                    </div>
                  </q-popup-proxy>
                </q-icon>
              </template>
            </q-input>
            <div class="dialog-box-input-element">
              <q-input
                v-model="reqComments"
                outlined
                color="orange"
                type="textarea"
                label= "Комментарии"
                counter
                maxlength="512"
              >
                <template v-slot:hint>
                  Количество символов:
                </template>
              </q-input>
            </div>

          </q-card-section>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn label="Отменить" outline style="color: #4f4f4f; margin: 20px;" v-close-popup />
          <q-btn :label="createBtnLabel"  style="background-color: #2F3542; color: white; margin: 20px;" :disable="createReqBtndisabled" v-close-popup @click="saveData"/>
        </q-card-actions>
      </q-card>
    </q-dialog>
    <q-dialog v-model="confirmDeleteDialog" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="delete" color="red" text-color="white"/>
          <span class="q-ml-sm"> Вы уверены удалить данную реквизицию?</span>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Отмена" color="blue" v-close-popup/>
          <q-btn flat label="Удалить" @click="deleteRequest" color="red" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>
    <q-dialog v-model="showPowerBiDialog">
      <q-card style="min-width: 1000px;">
        <q-card-section>
          <iframe style="overflow: hidden; position: relative; width: 970px; height: 700px;" src="https://app.powerbi.com/reportEmbed?reportId=718f1b32-b7f1-4600-b351-3b9a1e2a404f&autoAuth=true&ctid=30f55b9e-dc49-493e-a20c-0fbb510a0971&config=eyJjbHVzdGVyVXJsIjoiaHR0cHM6Ly93YWJpLWVhc3QtYXNpYS1hLXByaW1hcnktcmVkaXJlY3QuYW5hbHlzaXMud2luZG93cy5uZXQvIn0%3D%22" allowfullscreen="true" ></iframe>
        </q-card-section>
        <q-card-actions align="right">
          <q-btn flat label="Закрыть" color="blue" v-close-popup/>
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>

import { useMainStore } from 'stores/main-store'
import { exportFile } from 'quasar'
import { ref } from 'vue'
function wrapCsvValue (val, formatFn, row) {
  let formatted = formatFn !== void 0
    ? formatFn(val, row)
    : val

  formatted = formatted === void 0 || formatted === null
    ? ''
    : String(formatted)

  formatted = formatted.split('"').join('""')

  return `"${formatted}"`
}

export default {
  name: 'MainPage',
  setup () {
    const store = useMainStore()
    return {
      store,
      statusSearchValue: ref(null)
    }
  },
  data () {
    return {
      isSoftClick: false,
      loading: false,
      list: [
        {
          requisitionId: 1,
          assetNumber: '000.16.100',
          assetTypeCode: 'Dozer',
          stopDate: '2022-04-27 13:30:41',
          wrqReasonCode: 'Поломка',
          wrqNo: 'ремонтные комплекты',
          wrqTitle: 'самосвалов PMKT г/п 15-25 т',
          reqComments: 'Покрышки на колесах имеют разную степень износа',
          wrqPriorityCode: 'P1',
          dateExpected: '2022-04-27 13:30',
          status: 2
        },
        {
          requisitionId: 2,
          assetNumber: '000.16.100',
          assetTypeCode: 'Hitachi',
          stopDate: '2022-04-27 13:30:41',
          wrqReasonCode: 'Поломка',
          wrqNo: 'ремонтные комплекты',
          wrqTitle: 'самосвалов PMKT г/п 15-25 т',
          reqComments: 'Покрышки на колесах имеют разную степень износа',
          wrqPriorityCode: 'P1',
          dateExpected: '2022-04-27 13:30',
          status: 2
        },
        {
          requisitionId: 3,
          assetNumber: '000.16.100',
          assetTypeCode: 'Hitachi',
          stopDate: '2022-04-27 13:30:41',
          wrqReasonCode: 'Поломка',
          wrqNo: 'ремонтные комплекты',
          wrqTitle: 'самосвалов PMKT г/п 15-25 т',
          reqComments: 'Покрышки на колесах имеют разную степень износа',
          wrqPriorityCode: 'P1',
          dateExpected: '2022-04-27 13:30',
          status: 1
        },
        {
          requisitionId: 4,
          assetNumber: '000.16.100',
          assetTypeCode: 'Hitachi',
          stopDate: '2022-04-27 13:30:41',
          wrqReasonCode: 'Поломка',
          wrqNo: 'ремонтные комплекты',
          wrqTitle: 'самосвалов PMKT г/п 15-25 т',
          reqComments: 'Покрышки на колесах имеют разную степень износа',
          wrqPriorityCode: 'P1',
          dateExpected: '2022-04-27 13:30',
          status: 0
        }

      ],
      statusList: [
        {
          label: 'Ожидание',
          statusCode: 0,
          color: '#FFAF00'
        },
        {
          label: 'В работе',
          statusCode: 1,
          color: '#2589FF'
        },
        {
          label: 'Завершен',
          statusCode: 2,
          color: '#2ED13F'
        }
      ],
      pagination: {
        page: 1,
        rowsPerPage: 10,
        rowsNumber: 10
      },
      statusListModified: [
        {
          label: 'Все',
          statusCode: 0,
          color: '#FFAF00',
          isChecked: false
        },
        {
          label: 'Ожидание',
          statusCode: 101,
          color: '#FFAF00',
          isChecked: false
        },
        {
          label: 'В работе',
          statusCode: 100,
          color: '#2589FF',
          isChecked: false
        },
        {
          label: 'Завершен',
          statusCode: 102,
          color: '#2ED13F',
          isChecked: false
        }
      ],
      selectedReqId: -1,
      showAddDialog: false,
      confirmDeleteDialog: false,
      showSearchBtn: true,
      showPowerBiDialog: false,
      isForEdit: false,
      searchModalView: false,
      // бортовой номер при добавлении
      assetNumber: null,
      assetTypeCode: '',
      stopDate: new Date().toISOString().substring(0, 16).replace('T', ' '),
      wrqReasonCode: null,
      // рабочий наряд при добавлении
      wrqNo: null,
      wrqTitle: '',
      reqComments: '',
      wrqPriorityCode: '',
      dateExpected: new Date().toISOString().substring(0, 16).replace('T', ' '),
      assetNos: [],
      reasonNos: [],
      priorityNos: [],
      wrqNos: [],
      assetSearchNos: [],
      assetTypes: [],
      categorySearchValue: [],
      modifiedCategorySearchList: [],
      wrqNosSearch: [],
      // бортовой номер при поиска
      assetNumberSearch: null,
      // рабочий наряд при поиска
      wrqNoSearch: null,
      // Вид транспорта при поиска
      assetTypeCodeSearch: null,
      // Описание при поиска
      wrqTitleSearch: '',
      // Причина при поиска
      wrqReasonCodeSearch: null,
      // Приоритет при поиска
      wrqPriorityCodeSearch: null,
      assetTypeListForSearch: []
    }
  },
  mounted () {
    this.stopdate = new Date().toISOString().substring(0, 10)
    this.tentativeIssueDate = new Date().toISOString().substring(0, 16).replace('T', ' ')
    this.assetNos = this.getAssetNos
    this.assetTypes = this.getAssetTypes
    this.reasonNos = this.reasonList
    this.priorityNos = this.priorityList
    this.assetSearchNos = this.getAssetNos
    this.wrqNosSearch = this.wrqListSearch
    // this.pagination.rowsNumber = this.overallItemSize
  },
  async created () {
    // this.store.getAllReqList()
    const searchBody = {
      sizePerPage: this.pagination.rowsPerPage,
      page: this.pagination.page,
      // assetTypeCode: this.assetTypeCodeSearch?.code ?? '',
      assetTypeCode: '',
      // statusCode: this.statusSearchValue.length ? this.statusSearchValue[0].statusCode : '',
      statusCode: '',
      wrqReasonId: this.wrqReasonCodeSearch?.id ?? '',
      wrqPriorityId: this.wrqPriorityCodeSearch?.id ?? '',
      wrqNo: this.wrqNoSearch?.code ?? '',
      wrqTitle: this.wrqNoSearch?.name ?? '',
      assetCode: this.assetNumberSearch?.code ?? ''
    }
    // console.log('Search Body ', searchBody)
    await this.store.getSearchReqList(searchBody)
    setTimeout(this.setOverallItemSize, 1000)
    this.store.getAssetNoList()
    this.store.getAssetTypeList()
    this.store.getReasonList()
    this.store.getPriorityList()
    this.store.getLocationList()
    this.store.getOnholdList()
    this.store.getWrqListSearch()
  },
  methods: {
    onCatalogAddClicked (index) {
      this.$router.push({ name: 'catalog', params: { catalogIndex: index } })
    },
    setOverallItemSize () {
      this.pagination.rowsNumber = this.overallItemSize
      console.log('Pagination rows number ', this.pagination.rowsNumber)
    },
    searchBtnClickEvntCall () {
      const searchButton = document.querySelector('#clear-btn')
      console.log('Search Btn ', searchButton)
      searchButton.click()
    },
    showSearchModalView () {
      console.log('Status SearchValue ', this.statusSearchValue)
      this.assetTypeListForSearch = this.assetTypesForSearch
      this.showSearchBtn = false
      this.searchModalView = true
      const aN = this.assetNumberSearch
      const wN = this.wrqNoSearch
      const aT = this.assetTypeCodeSearch
      const wT = this.wrqTitleSearch
      const wR = this.wrqReasonCodeSearch
      const wP = this.wrqPriorityCodeSearch
      const sV = this.statusSearchValue
      const cV = this.categorySearchValue
      const tempStatusList = this.statusListModified.map(itm => itm.isChecked)
      const tempAssetTypeList = this.assetTypeListForSearch.map(itm => itm.isChecked)
      setTimeout(this.searchBtnClickEvntCall, 100)
      setTimeout(() => {
        this.assetNumberSearch = aN
        this.wrqNoSearch = wN
        this.assetTypeCodeSearch = aT
        this.wrqTitleSearch = wT
        this.wrqReasonCodeSearch = wR
        this.wrqPriorityCodeSearch = wP
        this.statusSearchValue = sV
        this.categorySearchValue = cV
        for (let i = 0; i < this.statusListModified.length; i++) {
          this.statusListModified[i].isChecked = tempStatusList[i]
        }
        for (let i = 0; i < this.assetTypeListForSearch.length; i++) {
          this.assetTypeListForSearch[i].isChecked = tempAssetTypeList[i]
        }
      }, 300)
    },
    hideSearchModalView () {
      this.resetSearchSelecteds(true)
      // this.resetAddDialogData()
      this.showSearchBtn = true
      this.searchModalView = false
      this.pagination = { page: 1, rowsPerPage: 4, rowsNumber: 10 }
      const searchBody = {
        sizePerPage: this.pagination.rowsPerPage,
        page: this.pagination.page,
        // assetTypeCode: this.assetTypeCodeSearch?.code ?? '',
        assetTypeCode: '',
        // statusCode: this.statusSearchValue.length ? this.statusSearchValue[0].statusCode : '',
        statusCode: '',
        wrqReasonId: '',
        wrqPriorityId: '',
        wrqNo: '',
        wrqTitle: '',
        assetCode: ''
      }
      // console.log('Search Body ', searchBody)
      this.store.getSearchReqList(searchBody)
    },
    async searchGetData (props) {
      console.log('Pagination NEW ', props.pagination)
      console.log('Pagination OLD ', this.pagination)
      this.loading = true
      /* assetTypeCode - Вид транспорта
      statusCode - Статус
      wrqReasonId - Причина
      wrqPriorityId - Приоритет
      wrqNo - Рабочий наряд
      wrqTitle - описание
      assetCode - Бортовой номер */
      // console.log('Status Search Value ', this.statusSearchValue)
      let stCodes = ''
      if (this.statusSearchValue) {
        const allItm = this.statusSearchValue.find(itm => itm.statusCode === 0)
        console.log('All Itm ', allItm ?? '')
        if (!allItm) {
          const codes = this.statusSearchValue.map(itm => itm.statusCode)
          // eslint-disable-next-line no-unused-vars
          stCodes = codes.join()
        }
      }
      // console.log('Category Search Value ', this.categorySearchValue)
      const typeCodes = this.categorySearchValue ? this.categorySearchValue.map(itm => itm.code) : null
      // console.log('Type Codes ', typeCodes)
      const searchBody = {
        sizePerPage: props.pagination.rowsPerPage,
        page: props.pagination.page,
        // assetTypeCode: this.assetTypeCodeSearch?.code ?? '',
        assetTypeCode: typeCodes?.length ? typeCodes.join() : '',
        // statusCode: this.statusSearchValue.length ? this.statusSearchValue[0].statusCode : '',
        statusCode: stCodes,
        wrqReasonId: this.wrqReasonCodeSearch?.id ?? '',
        wrqPriorityId: this.wrqPriorityCodeSearch?.id ?? '',
        wrqNo: this.wrqNoSearch?.code ?? '',
        wrqTitle: this.wrqNoSearch?.name ?? '',
        assetCode: this.assetNumberSearch?.code ?? ''
      }
      console.log('Search Body ', searchBody)
      this.store.getSearchReqList(searchBody).then(res => {
        console.log('End of Data fetch ', res)
        this.loading = false
        this.pagination.page = props.pagination.page
        this.pagination.rowsPerPage = props.pagination.rowsPerPage
      })
      setTimeout(this.setOverallItemSize, 500)
      this.showSearchBtn = true
      this.searchModalView = false
    },
    onRowClick (row) {
      console.log('clicked row: ', row)
      const id = row.requestId
      const assetId = row.assetCode
      const status = row.statusCode
      this.$router.push({ name: 'assetdetails', params: { id, assetId, status } })
    },
    resetAddDialogData () {
      this.assetNumber = null
      this.assetTypeCode = ''
      this.stopDate = new Date().toISOString().substring(0, 16).replace('T', ' ')
      this.wrqReasonCode = null
      this.wrqNo = ''
      this.wrqTitle = ''
      this.reqComments = ''
      this.wrqPriorityCode = ''
      this.dateExpected = new Date().toISOString().substring(0, 16).replace('T', ' ')
      this.wrqNos = []
      this.store.wrqList = []
    },
    resetSearchSelecteds () {
      this.assetNumberSearch = null
      this.wrqNoSearch = null
      this.assetTypeCodeSearch = null
      this.wrqTitleSearch = null
      this.wrqReasonCodeSearch = null
      this.wrqPriorityCodeSearch = null
      this.statusSearchValue = []
      this.categorySearchValue = []
      this.statusListModified.forEach(itm => {
        itm.isChecked = false
      })
      console.log('StatusSearchValue ', this.statusSearchValue)
      this.store.modifiedAssetTypeListForSearch.forEach(itm => { itm.isChecked = false })
    },
    saveData () {
      const body = {
        assetCode: this.assetNumber.value,
        // assetTypeCode: this.assetTypeCode.value,
        stopDate: this.stopDate,
        // stopDate: this.stopDate.replaceAll('/', '-') + ' 18:00',
        wrqReasonCode: this.wrqReasonCode.value,
        wrqNo: this.wrqNo?.code,
        wrqTitle: this.wrqNo?.name,
        reqComments: this.reqComments,
        wrqPriorityCode: this.wrqPriorityCode.priorityCode,
        dateExpected: this.dateExpected
        // dateExpected: this.dateExpected.replaceAll('/', '-') + ' 18:00'
      }
      console.log('BODY ', body)
      if (this.isForEdit) {
        this.store.updateAssetReq(body, this.selectedReqId)
      } else {
        this.store.createAssetReq(body)
      }
    },
    deleteRequest () {
      this.store.deleteRequest(this.selectedReqId)
    },
    onAddClick () {
      this.resetAddDialogData()
      this.showAddDialog = true
      this.isForEdit = false
    },
    onDeleteClick (id) {
      this.selectedReqId = id
      this.confirmDeleteDialog = true
    },
    onEditClick (row) {
      console.log('ROW ', row)
      this.selectedReqId = row.requestId
      this.store.getWrqList(row.assetCode)
      this.assetNumber = this.store.modifiedAssetNoList.find(itm => itm.code === row.assetCode.trim())
      this.assetTypeCode = this.assetNumber.name
      // this.assetTypeCode = this.store.modifiedAssetTypeList.find(itm => itm.code === row.assetTypeCode)
      this.stopDate = row.stopDate
      // this.stopDate = row.stopDate.substring(0, 10)
      this.wrqReasonCode = this.store.modifiedReasonList.find(itm => itm.wrqReasonCode === row.wrqReasonCode)
      this.wrqNo = { code: row.wrqNo, name: row.wrqTitle }
      // this.wrqTitle = row.wrqTitle
      this.reqComments = row.reqComments
      this.wrqPriorityCode = this.store.modifiedPriorityList.find(itm => itm.priorityCode === row.wrqPriorityCode)
      this.dateExpected = row.dateExpected
      // this.dateExpected = row.dateExpected.substring(0, 10)
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
    async selectAssetNo (value) {
      if (value) {
        await this.store.getWrqList(value.value)
        this.wrqNo = null
        this.assetTypeCode = this.assetNumber.name
        this.wrqNos = this.wrqList
        console.log('SelectedAssetNo ', value)
      } else {
        this.assetTypeCode = ''
      }
    },
    selectAssetNoSearch (value) {
      if (value) {
        console.log('SelectedAssetNo ', value)
      } else {
        this.assetTypeCode = ''
      }
    },

    selectStatusSearch (value) {
      console.log('Select Status Search', value)
      if (value) {
        console.log('Status Value ', value)
        console.log('Status Model ', this.statusSearchValue)
        this.statusListModified.forEach(itm => {
          itm.isChecked = false
        })
        this.statusSearchValue.forEach(itm => {
          itm.isChecked = true
          console.log('Search element ', itm.label)
        })
      }
    },
    selectCategorySearch (value) {
      if (value) {
        console.log('Category Value ', value)
        console.log('Category Model ', this.categorySearchValue)
        this.store.modifiedAssetTypeListForSearch.forEach(itm => {
          itm.isChecked = false
        })
        this.categorySearchValue.forEach(itm => {
          itm.isChecked = true
          console.log('Search element ', itm.label)
        })
      }
    },
    filterAssetNo (val, update) {
      update(() => {
        this.assetNos = val ? this.getAssetNos.filter(itm => itm.code.includes(val) || itm.number.includes(val)) : this.getAssetNos
      })
    },
    filterSearchAssetNo (val, update) {
      update(() => {
        this.assetSearchNos = val ? this.getAssetNos.filter(itm => itm.code.includes(val) || itm.number.includes(val)) : this.getAssetNos
      })
    },
    filterReasonList (val, update) {
      update(() => {
        this.reasonNos = val ? this.reasonList.filter(itm => itm.label.toLowerCase().includes(val.toLowerCase())) : this.reasonList
      })
    },
    filterPriorityList (val, update) {
      update(() => {
        this.priorityNos = val ? this.priorityList.filter(itm => itm.label.toLowerCase().includes(val.toLowerCase())) : this.priorityList
      })
    },
    filterWrqList (val, update) {
      update(() => {
        this.wrqNos = val ? this.wrqList.filter(itm => itm.code.includes(val)) : this.wrqList
      })
    },
    filterWrqSearchNoList (val, update) {
      update(() => {
        this.wrqNosSearch = val ? this.wrqListSearch.filter(itm => itm.code.includes(val)) : this.wrqListSearch
      })
    },
    filterWrqSearchDesList (val, update) {
      update(() => {
        this.wrqNosSearch = val ? this.wrqListSearch.filter(itm => itm.name.toLowerCase().includes(val.toLowerCase())) : this.wrqListSearch
      })
    },
    filterWrqDesList (val, update) {
      update(() => {
        this.wrqNos = val ? this.wrqList.filter(itm => itm.name.toLowerCase().includes(val.toLowerCase())) : this.wrqList
      })
    },
    filterAssetTypeListSearch (val, update) {
      update(() => {
        this.assetTypeListForSearch = val ? this.assetTypesForSearch.filter(itm => itm.label.toLowerCase().includes(val.toLowerCase())) : this.assetTypesForSearch
      })
    },
    goToPowerBI () {
      // console.log('Go to PowerBI')
      this.showPowerBiDialog = true
    },
    exportTable () {
      const filteredColumns = this.columns.filter((col) => col.name !== 'action')
      const content = [filteredColumns.map(col => wrapCsvValue(col.label))].concat(
        this.rows.map(row => filteredColumns.map(col => wrapCsvValue(
          typeof col.field === 'function'
            ? col.field(row)
            : row[col.field === void 0 ? col.name : col.field],
          col.format,
          row
        )).join(','))
      ).join('\r\n')

      const today = new Date().toISOString().slice(0, 10)
      const status = exportFile(
        `Maintenance-${today}.csv`,
        '\ufeff' + content,
        'text/csv'
      )

      if (status !== true) {
        this.$q.notify({
          message: 'Не удалось скачать файл',
          position: 'center',
          color: '#E2A55C'
        })
      } else {
        this.$q.notify({
          message: 'Скачивание успешно завершно',
          position: 'center',
          color: '#E2A55C'
        })
      }
    }
  },

  computed: {
    createReqBtndisabled () {
      return !this.assetNumber || !this.assetTypeCode || !this.wrqPriorityCode || !this.wrqReasonCode
    },
    searchBtndisabled () {
      return false
    },
    createReqDialogTitle () {
      return this.isForEdit ? 'Редактирование заявки' : 'Новая заявка'
    },
    createBtnLabel () {
      return this.isForEdit ? 'Сохранить' : 'Добавить'
    },
    filteredByStatusList () {
      if (this.store.requisitionList) {
        return this.store.requisitionList
      }
      return []
    },
    overallItemSize () {
      return this.store.totalItems
    },
    rows () {
      // console.log('ROWS Computed Method')
      this.filteredByStatusList.forEach((itm, i) => {
        itm.index = i + 1
      })
      // console.log('main list ', this.filteredByStatusList)
      return this.filteredByStatusList
    },
    getAssetNos () {
      return this.store.modifiedAssetNoList
    },
    getAssetTypes () {
      return this.store.modifiedAssetTypeList
    },
    assetTypesForSearch () {
      // console.log('AssetTypesForSearch ', this.store.modifiedAssetTypeListForSearch)
      return this.store.modifiedAssetTypeListForSearch
    },
    reasonList () {
      return this.store.modifiedReasonList.filter(itm => itm.isActive === 'Y')
    },
    priorityList () {
      return this.store.modifiedPriorityList.filter(itm => itm.isActive === 'Y')
    },
    wrqList () {
      return this.store.modifiedWrqList
    },
    wrqListSearch () {
      return this.store.modifiedWrqListSearch
    },
    columns () {
      return [
        {
          name: 'id',
          required: true,
          label: '№',
          align: 'center',
          style: 'width: 20px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.index
        },
        {
          name: 'bortNo',
          required: true,
          label: 'Бортовой номер',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis; max-with: 80px;',
          field: row => row.assetCode
        },
        {
          name: 'transportKind',
          required: true,
          label: 'Вид транспорта',
          align: 'center',
          style: 'min-width: 30px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.assetTypeCode
        },
        {
          name: 'stopDate',
          required: true,
          label: 'Дата остановки',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.stopDate
        },
        {
          name: 'cause',
          required: true,
          label: 'Причина',
          align: 'center',
          style: 'min-width: 30px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.wrqReasonName
        },
        {
          name: 'outfit',
          required: true,
          label: 'Рабочий наряд',
          align: 'center',
          style: 'min-width: 40px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.wrqNo
        },
        {
          name: 'description',
          required: true,
          label: 'Описание рабочего наряда',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.wrqTitle
        },
        {
          name: 'comment',
          required: true,
          label: 'Комментарии',
          align: 'center',
          style: 'min-width: 40px; max-width: 270px; overflow-wrap: break-word; text-overflow: ellipsis;',
          field: row => row.reqComments
        },
        {
          name: 'priority',
          required: true,
          label: 'Приоритет',
          align: 'center',
          style: 'min-width: 30px',
          field: row => row.wrqPriorityName
        },
        {
          name: 'tentativeIssueDate',
          required: true,
          label: 'Ожидаемая дата запуска',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.dateExpected
        },
        {
          name: 'issueDate',
          required: true,
          label: 'Дата запуска',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.dateFinished
        },
        {
          name: 'status',
          required: true,
          label: 'Статус',
          align: 'center',
          style: 'min-width: 40px',
          field: row => row.statusCode
        },
        {
          name: 'action',
          required: true,
          label: 'Действия',
          align: 'center',
          style: 'min-width: 35px'
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
  min-width: 30px;
  overflow-wrap: break-word;
  text-overflow: ellipsis
}
.mainContainer {
  position: relative;
  display: flex;
  flex-direction: column;
  gap: 30px;
  top: 50px;
  padding: 0 1%;
  @media (min-width: 1500px) {
    padding: 0 3%;
  }
}

.checkbox-not-clickable {
  pointer-events: none;
}
.buttons-row {
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.dropdown-filter {
  position: relative;
  display: flex;
  flex-direction: row;
  justify-content: start;
  align-items: center;
  gap: 10px;
}

.add-btn {
  position: relative;
  margin-right: 10%;
}
.add-dialog-title {
  font-family: Rubik;
  font-weight: 700;
  font-size: 26px;
}
.search-dialog-title {
  font-weight: 600;
  font-size: 20px;
  line-height: 1rem;
  margin-bottom: 10px;
}
.searchSelectItem{
  width: 100%;
}
.dialog-box-input-element {
  width: 95%;
  margin-bottom: 25px;
  //background: #F8F8F8;
  //filter: drop-shadow(0px 4px 7px rgba(139, 139, 139, 0.25));
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

/*********************************MEDIA STYLES*********************************/
/*
@media screen and (max-width:1370px) {
  .searchSelectItem{
    width: 404px;
  }
}
@media screen and (min-width:1371px) {
  .searchSelectItem{
    width: 500px;
  }
}
@media screen
and (max-device-width: 1200px)
and (-webkit-min-device-pixel-ratio: 2)
and (min-resolution: 92dpi) {
  .searchSelectItem{
    width: 304px;
  }
} */
</style>
