import {createLocalVue, mount} from '@vue/test-utils'
import VueRouter from 'vue-router'
import ActivityPageInfo from '@/components/modules/ActivityPageInfo.vue'

const localVue = createLocalVue()
localVue.use(VueRouter)
const router = new VueRouter()

describe('Activity page display check', () => {
  const wrapper = mount(ActivityPageInfo, {
    localVue,
    router,
    propsData: {
      activityInfo: {
        name: "NZ AUS Trail Running Walking",
        start: "25th April 2020",
        end: "26th April 2020",
        location: "Kaikoura, New Zealand",
        duration: "5 minutes",
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
    }
  })

  it('Activity title should be visible ', () => {
    expect(wrapper.find("#activityTitle").isVisible()).toBe(true)
    expect(wrapper.find("#activityTitle").text()).toBe("NZ AUS Trail Running Walking")
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
    expect(wrapper.find("#activityStartDate").text()).toBe("Start date: 25th April 2020")
  })

  it('Activity end date should be visible ', () => {
    expect(wrapper.find("#activityEndDate").isVisible()).toBe(true)
    expect(wrapper.find("#activityEndDate").text()).toBe("End date: 26th April 2020")
  })

  // *** come back to this after backend is implemented, this will fail ***
  it('Activity duration should be visible ', () => {
    expect(wrapper.find("#activityDuration").isVisible()).toBe(true)
    expect(wrapper.find("#activityDuration").text()).toBe("5 minutes")
  })

  it('Activity end date should be visible ', () => {
    expect(wrapper.find("#activityTypeListing").isVisible()).toBe(true)
    expect(wrapper.find("#activityTypeListing").text()).toMatch(/Hiking/)
    expect(wrapper.find("#activityTypeListing").text()).toMatch(/Running/)
  })

})