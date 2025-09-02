<template>
  <div>
    <h2>Books</h2>
    <ul>
      <li v-for="book in books" :key="book">{{ book }}</li>
    </ul>
  </div>
</template>

<script>
import { getBooks } from '@/api/book'

export default {
  data() {
    return {
      books: []
    }
  },
  created() {
    getBooks().then(data => {
      this.books = data
    }).catch(err => {
      console.error(err)
    })
  }
}
</script>

