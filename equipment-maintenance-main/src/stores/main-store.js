import { defineStore } from 'pinia'
import { deleteRequest, getRequest, postLogin, postRequest, putRequest } from './http-requests'
import { urlMain } from 'stores/main-config'

const url = urlMain
export const useMainStore = defineStore('equipmentMaintenance', {
  state: () => ({
    requisitionList: [],
    assetNoList: [],
    assetTypeList: [],
    reasonList: [],
    locationList: [],
    priorityList: [],
    onholdList: [],
    wrqList: [],
    assetDetailsList: [],
    detailsStageList: [],
    wrqListSearch: [],
    currentWrq: null,
    totalPages: 1,
    totalItems: -1
  }),
  getters: {
    modifiedAssetNoList: (state) => {
      const arr = []
      state.assetNoList.map(el => (
        arr.push({
          label: el.code,
          value: el.code,
          code: el.code,
          name: el.name,
          number: el.number
        })
      ))
      return arr
    },
    modifiedAssetTypeList: (state) => {
      const arr = []
      state.assetTypeList.map(el => (
        arr.push({
          label: el.code + '  ' + el.name,
          value: el.code,
          code: el.code,
          name: el.name
        })
      ))
      return arr
    },
    modifiedAssetTypeListForSearch: (state) => {
      const arr = []
      state.assetTypeList.map(el => (
        arr.push({
          label: el.name,
          code: el.code,
          name: el.name,
          isChecked: false
        })
      ))
      return arr
    },
    modifiedReasonList: (state) => {
      const arr = []
      state.reasonList.map(el => (
        arr.push({
          label: el.name,
          value: el.code,
          id: el.id,
          wrqReasonCode: el.code,
          wrqReasonName: el.name,
          isActive: el.isActive
        })
      ))
      return arr
    },
    modifiedPriorityList: (state) => {
      const arr = []
      state.priorityList.map(el => (
        arr.push({
          label: el.name,
          value: el.code,
          id: el.id,
          priorityCode: el.code,
          priorityName: el.name,
          isActive: el.isActive
        })
      ))
      return arr
    },
    modifiedWrqList: (state) => {
      const arr = []
      state.wrqList.map(el => (
        arr.push({
          code: el.code,
          name: el.name
        })
      ))
      return arr
    },
    modifiedWrqListSearch: (state) => {
      const arr = []
      state.wrqListSearch.map(el => (
        arr.push({
          code: el.code,
          name: el.name
        })
      ))
      return arr
    },
    modifiedLocationList: (state) => {
      const arr = []
      state.locationList.map(el => (
        arr.push({
          label: el.name ?? '',
          id: el.id,
          code: el.code ?? '',
          name: el.name ?? '',
          isActive: el.isActive
        })
      ))
      return arr
    },
    modifiedOnholdList: (state) => {
      const arr = []
      state.onholdList.map(el => (
        arr.push({
          label: el.name ?? '',
          id: el.id,
          isActive: el.isActive,
          code: el.code ?? '',
          name: el.name ?? ''
        })
      ))
      return arr
    },
    modifiedDetailsStageList: (state) => {
      const arr = []
      state.detailsStageList.map(el => (
        arr.push({
          label: el.name,
          value: { code: el.code, label: el.name },
          code: el.code,
          name: el.name
        })
      ))
      return arr
    }
  },

  actions: {
    async login (body) {
      const res = await postLogin(body)
      if (res) {
        return res
      } else {
        console.log('Error login')
      }
    },
    getAllReqList () {
      getRequest(`${url}/pm-requests`).then(res => {
        console.log('PM-REQUESTS Overall', res)
        this.totalItems = res.totalItems
        this.totalPages = res.totalPages
        this.requisitionList = res.content
      })
    },
    async getReqListWithId (id) {
      getRequest(`${url}/pm-requests/${id}`).then(res => {
        this.currentWrq = res
      })
    },
    async getSearchReqList (searchBody) {
      const searchUrl = `${url}/pm-requests?assetTypeCode=${searchBody.assetTypeCode}&statusCode=${searchBody.statusCode}&wrqReasonId=${searchBody.wrqReasonId}&wrqPriorityId=${searchBody.wrqPriorityId}&wrqNo=${searchBody.wrqNo}&wrqTitle=${searchBody.wrqTitle}&assetCode=${searchBody.assetCode}&size=${searchBody.sizePerPage}&page=${searchBody.page}`
      getRequest(searchUrl).then(res => {
        this.totalPages = res.totalPages
        this.totalItems = res.totalItems
        this.requisitionList = res.content
        // console.log('PM-REQUESTS ', this.requisitionList)
        // console.log('PM-REQUESTS Total Items', this.totalItems)
      })
    },
    getAssetNoList () {
      getRequest(`${url}/reference/manas/asset`).then(res => {
        this.assetNoList = res
      })
    },
    getAssetTypeList () {
      getRequest(`${url}/reference/manas/asset-type/`).then(res => {
        this.assetTypeList = res
      })
    },
    getReasonList () {
      getRequest(`${url}/reference/internal/reason`).then(res => {
        this.reasonList = res
      })
    },
    getPriorityList () {
      getRequest(`${url}/reference/internal/priority`).then(res => {
        this.priorityList = res
      })
    },
    getLocationList () {
      getRequest(`${url}/reference/internal/location`).then(res => {
        this.locationList = res
      })
    },
    getOnholdList () {
      getRequest(`${url}/reference/internal/on-hold`).then(res => {
        this.onholdList = res
      })
    },
    getDetailsStageList () {
      getRequest(`${url}/reference/internal/stage`).then(res => {
        this.detailsStageList = res
      })
    },
    async getWrqList (assetNo) {
      getRequest(`${url}/reference/manas/wrq?assetCode=${assetNo}`).then(res => {
        this.wrqList = res
      })
    },
    getWrqListSearch () {
      getRequest(`${url}/reference/manas/wrq?`).then(res => {
        this.wrqListSearch = res
      })
    },
    getAssetDetailsList (assetNo) {
      getRequest(`${url}/pm-requests/${assetNo}/details`).then(res => {
        console.log('Asset Details: ', res)
        this.assetDetailsList = res
      })
    },
    deleteAssetDetail (id, assetCode, data) {
      deleteRequest(`${url}/pm-requests/${id}/details/${assetCode}`, data).then((res) => {
        this.getAssetDetailsList(id)
        this.getReqListWithId(id)
      })
        .catch((error) => {
          console.log('Error deleteDetails', error)
        })
    },
    createAssetDetails (data, id) {
      postRequest(`${url}/pm-requests/${id}/details/`, data).then((res) => {
        this.getAssetDetailsList(id)
        this.getReqListWithId(id)
        return res
      })
        .catch((error) => {
          console.log('Error CreateAssetdetails', error)
        })
    },
    updateAssetDetails (data, id, assetCode) {
      putRequest(`${url}/pm-requests/${id}/details/${assetCode}`, data).then((res) => {
        this.getAssetDetailsList(id)
        this.getReqListWithId(id)
        return res
      })
        .catch((error) => {
          console.log('Error CreateRequest', error)
        })
    },
    createAssetReq (data) {
      postRequest(`${url}/pm-requests/`, data).then((res) => {
        this.getAllReqList()
        return res
      })
        .catch((error) => {
          console.log('Error CreateRequest', error)
        })
    },
    updateAssetReq (data, id) {
      putRequest(`${url}/pm-requests/${id}`, data).then((res) => {
        this.getAllReqList()
        return res
      })
        .catch((error) => {
          console.log('Error CreateRequest', error)
        })
    },
    deleteRequest (id) {
      deleteRequest(`${url}/pm-requests/` + id).then((res) => {
        this.getAllReqList()
      })
        .catch((error) => {
          console.log('Error deleteRequest', error)
        })
    },
    createOnholdReference (data) {
      postRequest(`${url}/reference/internal/on-hold/`, data).then((res) => {
        this.getOnholdList()
        return res
      })
        .catch((error) => {
          console.log('Error CreateRequest', error)
        })
    },
    updateOnholdReference (data, id) {
      putRequest(`${url}/reference/internal/on-hold/${id}`, data).then((res) => {
        this.getOnholdList()
        return res
      })
        .catch((error) => {
          console.log('Error updateOnhold', error)
        })
    },
    deleteOnholdReference (id) {
      deleteRequest(`${url}/reference/internal/on-hold/` + id).then((res) => {
        this.getOnholdList()
      })
        .catch((error) => {
          console.log('Error deleteOnhold', error)
        })
    },
    createLocationReference (data) {
      postRequest(`${url}/reference/internal/location/`, data).then((res) => {
        this.getLocationList()
        return res
      })
        .catch((error) => {
          console.log('Error CreateRequest', error)
        })
    },
    updateLocationReference (data, id) {
      putRequest(`${url}/reference/internal/location/${id}`, data).then((res) => {
        this.getLocationList()
        return res
      })
        .catch((error) => {
          console.log('Error updateLocation', error)
        })
    },
    createReasonReference (data) {
      postRequest(`${url}/reference/internal/reason/`, data).then((res) => {
        this.getReasonList()
        return res
      })
        .catch((error) => {
          console.log('Error CreateReason', error)
        })
    },
    updateReasonReference (data, id) {
      putRequest(`${url}/reference/internal/reason/${id}`, data).then((res) => {
        this.getReasonList()
        return res
      })
        .catch((error) => {
          console.log('Error updateReason', error)
        })
    },
    createPriorityReference (data) {
      postRequest(`${url}/reference/internal/priority/`, data).then((res) => {
        this.getPriorityList()
        return res
      })
        .catch((error) => {
          console.log('Error CreatePriority', error)
        })
    },
    updatePriorityReference (data, id) {
      putRequest(`${url}/reference/internal/priority/${id}`, data).then((res) => {
        this.getPriorityList()
        return res
      })
        .catch((error) => {
          console.log('Error updatePriority', error)
        })
    }

  }
})
