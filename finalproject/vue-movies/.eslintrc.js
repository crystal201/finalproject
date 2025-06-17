module.exports = {
  root: true,
  env: {
    browser: true,
    node: true
  },
  parserOptions: {
    parser: '@babel/eslint-parser',
    requireConfigFile: false,
    babelOptions: {
      presets: ['@babel/preset-env']
    }
  },
  extends: [
    '@nuxtjs',
  ],

  rules: {
    'vue/html-closing-bracket-newline': ['error', {
      'singleline': 'never',
      'multiline': 'never'
    }],
    'vue/no-v-html': ['off'],
    'comma-dangle': ['error', 'always-multiline'],
    'semi': ['error', 'always'],
    'space-before-function-paren': ['error', 'always'],
    'no-console': ['warn'],
    'vue/multi-word-component-names': 'off',
    'space-before-function-paren': 'off',
    'comma-dangle': 'off',
    'eol-last': 'off',
    'indent': 'off',
    'no-trailing-spaces': 'off'
  }
}