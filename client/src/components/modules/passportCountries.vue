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
    props: {
      passports: Array,
    },
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
        /*
          Function uses the country api and checks it against the ist of passport countries selected by the user and then
          creates a dictionary of name mapped to the country code which is further used to retrieve the flags and display
          them on the profile page.
         */
        startUp() {
            axios.get(COUNTRIES_URL)
                .then((response) => {
                    var country_dicts = []
                    const data = response.data
                    for (var each_country of data) {
                        for(var country of this.passports){
                            if(country == each_country.name){
                                country_dicts.push([
                                    each_country.name,
                                    "https://restcountries.eu/data/".concat(each_country.alpha3Code.toLowerCase(), ".svg")
                                ]);
                            }
                        }
                    }
                    this.country_dicts = country_dicts
                })
                .catch(error => console.log(error))
        }
    },
  }
</script>
