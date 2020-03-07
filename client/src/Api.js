import axios from 'axios'

const SERVER_URL = 'https://4967d4f4-8301-42d1-a778-e3d150633644.mock.pstmn.io'
console.log(SERVER_URL + "@@@");

const instance = axios.create({
  baseURL: SERVER_URL,
  timeout: 5000
});

export default {
  // (C)reate
  createNew: (name) => instance.post('students', {name}),
  // (R)ead
  getAll: () => instance.get('students', {
    transformResponse: [function (data) {
      return data? JSON.parse(data)._embedded.students : data;
    }]
  }),
  // (U)pdate
  updateForId: (id, name) => instance.put('students/'+id, {name}),
  // (D)elete
  removeForId: (id) => instance.delete('students/'+id)
}
