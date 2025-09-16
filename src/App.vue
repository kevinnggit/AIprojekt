
<template>
  <div>
    {{text.split('salutation').reverse().join('')}}
  </div>
</template>

<script>
  export default {
    data () {
      return {
        isButtonDisabled: true
      };
    },
    methods: {

    }
  };
</script>

<style>

</style>
