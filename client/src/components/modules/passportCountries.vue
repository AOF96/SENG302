<template>
  <div class="profileModule">
    <h1>Passport Countries</h1>
    <div id="passportCountries">
        <div class="passportCountry">
            <img src="https://restcountries.eu/data/nzl.svg">
            <h2 v-for="country in user.passports" v-bind:key="country">
                    {{country}}
            </h2>
            <div class="floatClear"></div>
        </div>
    </div>
  </div>
</template>

<script>
    import {mapGetters} from "vuex";


  export default {

    computed: {
        ...mapGetters(['user']),
    },
    methods: {
    }
  }
</script>
