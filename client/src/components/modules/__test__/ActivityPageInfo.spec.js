import {createLocalVue, mount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import ActivityPageInfo from '@/components/modules/ActivityPageInfo.vue'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()

let activityInfo = {
  name: "NZ AUS Trail Running Walking",
    start: "2020-02-20T08:00:00+1300",
    end: "2020-02-20T08:00:00+1300",
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
  const wrapper = mount(ActivityPageInfo, {
    localVue,
    router,
    propsData: {
      activityInfo: activityInfo
    }
  })

  it('Activity title should be visible ', () => {
    expect(wrapper.find("#activityPageTitle").isVisible()).toBe(true)
    expect(wrapper.find("#activityPageTitle").text()).toBe("NZ AUS Trail Running Walking")
  })

  it('Activity description should be visible ', () => {
    expect(wrapper.find("#activityDescription").isVisible()).toBe(true)
    expect(wrapper.find("#activityDescription").text()).toBe("Trail running is a popular sport which involves running trails through challenging terrain.")
  })

  // *** come back to this after backend is implemented, this will fail ***
  it('Activity location should be visible ', () => {
    expect(wrapper.find("#activityLocation").isVisible()).toBe(true)
    expect(wrapper.find("#activityLocation").text()).toBe("Kaikoura, New Zealand")
  })

  // *** come back to this after backend is implemented, this will fail ***
  it('Activity start date should be visible ', () => {
    expect(wrapper.find("#activityStartDate").isVisible()).toBe(true)
    expect(wrapper.find("#activityStartDate").text()).toBe("Starts Thu, 20 February 2020 8:00 am")
  })

  it('Activity end date should be visible ', () => {
    expect(wrapper.find("#activityEndDate").isVisible()).toBe(true)
    expect(wrapper.find("#activityEndDate").text()).toBe("Ends Thu, 20 February 2020 8:00 am")
  })

  it('Activity end date should be visible ', () => {
    expect(wrapper.find("#activityTypeListing").isVisible()).toBe(true)
    expect(wrapper.find("#activityTypeListing").text()).toMatch(/Hiking/)
    expect(wrapper.find("#activityTypeListing").text()).toMatch(/Running/)
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
  const wrapper = mount(ActivityPageInfo, {
    localVue,
    router,
    propsData: {
      activityInfo: activityInfo2
    }
  })

  it('Activity start and end date should not be visible ', () => {
    expect(wrapper.find("#activityEndDate").exists()).toBe(false)
    expect(wrapper.find("#activityStartDate").exists()).toBe(false)
  })
})

