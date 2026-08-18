exports.config = {
  tests: './tests/**/*.test.js',
  output: './tests/output',
  helpers: {
    Playwright: {
      url: 'http://localhost:3000',
      browser: 'chromium',
      show: false,
      windowSize: '1280x720',
      chromium: {
        args: ['--no-sandbox', '--disable-setuid-sandbox']
      }
    }
  },
  include: {
    I: './steps_file.js'
  },
  name: 'Online-Store-FE-E2E'
};
