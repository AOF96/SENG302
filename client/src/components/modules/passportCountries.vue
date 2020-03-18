<template>
  <div class="profileModule">
    <h1>Passport Countries</h1>
    <div id="passportCountries">
        <div class="passportCountry">
            <img src="https://restcountries.eu/data/nzl.svg">
            <h2 v-for="country in passport_countries" v-bind:key="country">
                    {{country}}
            </h2>
            <div class="floatClear"></div>
        </div>
    </div>
  </div>
</template>

<script>
  import {userInfo} from "../../globals";

  export default {
    data () {
      return {
          passport_countries: userInfo.passportCountries,

      }
    },

       mounted() {
         console.log(this.passport_countries);
       },
    computed: {
      // validation() {
      //   return {
      //     oldPassword: this.password !== '',
      //     match: this.newPassword == this.confirmPassword,
      //     length: /.{8,}/.test(this.newPassword),
      //     number: /\d/.test(this.newPassword),
      //     uppercase: /[A-Z]/.test(this.newPassword),
      //   }
      // }
    },
    methods: {
      // submitPasswordChange() {
      //   if(this.validation.oldPassword){
      //     if(this.validation.match){
      //       if(this.validation.length){
      //         if(this.validation.number){
      //           if(this.validation.uppercase){
      //             alert("Password updated")
      //           }else{alert("Must contain at least one uppercase character.");}
      //         }else{alert("Must contain at least one number.");}
      //       }else{alert("Password length must at least be 8");}
      //     }else{alert("New passwords must match");}
      //   }else{alert("Enter your old password");}
      // }
    }
  }
</script>
