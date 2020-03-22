<template>
  <div class="profileModule">
    <h1>Passport Countries</h1>
    <div id="passportCountries">
        <div class="passportCountry" v-for="country_dict in country_dicts" v-bind:key="country_dict">
            <h2>{{country_dict[0]}}</h2>
            <img :src="country_dict[1]">
            <div class="floatClear"></div>
        </div>
    </div>
  </div>
</template>

<script>
    import {mapGetters} from "vuex";
    import axios from "axios";
    const COUNTRIES_URL = 'https://restcountries.eu/rest/v2/all'

  export default {
      data() {
          return {
              country_dicts: []
          }
      },
      computed: {
          ...mapGetters(['user'])
      },
      mounted() {
          this.startUp()
      },
      methods: {
          startUp() {
              axios.get(COUNTRIES_URL)
                  .then((response) => {
                      const country_dicts = []
                      const data = response.data
                      for (let each_country of data) {
                          for(let country of this.user.passports){
                              if(country == each_country.name){
                                  country_dicts.push([
                                      each_country.name,
                                      "https://restcountries.eu/data/".concat(each_country.alpha3Code.toLowerCase(), ".svg")
                                  ]);
                              }
                          }
                      }
                      this.country_dicts = country_dicts
                      console.log(country_dicts)
                      console.log(this.country_dicts)
                  })
                  .catch(error => console.log(error))
          }
      },
  }
</script>
