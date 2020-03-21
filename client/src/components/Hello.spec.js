import { mount } from '@vue/test-utils'
import Hello from './Hello.vue'

describe('Counter.vue', () => {
  test('clicking button increments the counter value by 1', () => {
    const wrapper = mount(Hello)
    expect(wrapper.text()).toContain('counter: 0')
    wrapper.find('button').trigger('click')
  })
})