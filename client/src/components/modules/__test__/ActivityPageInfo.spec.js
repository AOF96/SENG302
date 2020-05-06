import {createLocalVue, mount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import ActivityPageInfo from '@/components/modules/ActivityPageInfo.vue'
import Vuex from 'vuex'


const localVue = createLocalVue()
localVue.use(VueRouter)
localVue.use(Vuex)
const router = new VueRouter()

let activityInfo = {
  name: "NZ AUS Trail Running Walking",
  start_time: "2020-02-20T08:00:00+1300",
  end_time: "2020-02-20T08:00:00+1300",
  location: "Kaikoura, New Zealand",
  continuous: false,
  description: "Trail running is a popular sport which involves running trails through challenging terrain.",
  activityTypes: [
    {
      "type_id": 6,
      "name": "Hiking"
    },
    {
      "type_id": 7,
      "name": "Running"
    },
  ]
}

describe('Activity page display check', () => {

  let getters
  let store
  let wrapper

  beforeEach(() => {
    getters = {
      activity: () => activityInfo
    }

    store = new Vuex.Store({
      getters
    })

    wrapper = mount(ActivityPageInfo, {
      store,
      localVue,
      router
    })

  })


  it('Activity title should be visible ', () => {
    expect(wrapper.find("#activityPageTitle").isVisible()).toBe(true)
    expect(wrapper.find("#activityPageTitle").text()).toBe("NZ AUS Trail Running Walking")
  })

   it('Activity description should be visible ', () => {
     expect(wrapper.find("#activityPageDescription").isVisible()).toBe(true)
     expect(wrapper.find("#activityPageDescription").text()).toBe("Trail running is a popular sport which involves running trails through challenging terrain.")
   })

   // *** come back to this after backend is implemented, this will fail ***
   it('Activity location should be visible ', () => {
     expect(wrapper.find("#activityPageLocation").isVisible()).toBe(true)
     expect(wrapper.find("#activityPageLocation").text()).toBe("Kaikoura, New Zealand")
   })

   // *** come back to this after backend is implemented, this will fail ***
   it('Activity start date should be visible ', () => {
     expect(wrapper.find("#activityPageStartDate").isVisible()).toBe(true)
     expect(wrapper.find("#activityPageStartDate").text()).toBe("Start date: Thu, 20 February 2020 8:00 am")
   })

   it('Activity end date should be visible ', () => {
     expect(wrapper.find("#activityPageEndDate").isVisible()).toBe(true)
     expect(wrapper.find("#activityPageEndDate").text()).toBe("End date: Thu, 20 February 2020 8:00 am")
   })

   it('Activity end date should be visible ', () => {
     expect(wrapper.find("#activityPageTypeListing").isVisible()).toBe(true)
     expect(wrapper.find("#activityPageTypeListing").text()).toMatch(/Hiking/)
     expect(wrapper.find("#activityPageTypeListing").text()).toMatch(/Running/)
   })

})

 let activityInfo2 = {
   name: "NZ AUS Trail Running Walking",
   start: "25th April 2020",
   end: "26th April 2020",
   location: "Kaikoura, New Zealand",
   continuous: true,
   description: "Trail running is a popular sport which involves running trails through challenging terrain.",
   activityTypes: [
     {
       "type_id": 6,
       "name": "Hiking"
     },
     {
       "type_id": 7,
       "name": "Running"
     },
   ]
 }

 describe('Activity page should not display check', () => {

   let getters
   let store
   let wrapper

   beforeEach(() => {
     getters = {
       activity: () => activityInfo2
     }

     store = new Vuex.Store({
       getters
     })

     wrapper = mount(ActivityPageInfo, {
       store,
       localVue,
       router
     })


     it('Activity start and end date should not be visible ', () => {
       expect(wrapper.find("#activityEndDate").exists()).toBe(false)
       expect(wrapper.find("#activityStartDate").exists()).toBe(false)
     })
   })
 })