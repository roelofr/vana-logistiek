export const registerIcons = () => {
  fetch('/logo.svg', {
    cache: 'force-cache',
    mode: 'no-cors',
    credentials: 'omit'
  })
    .then(res => {
      if (!res.ok)
        throw new Error(`HTTP response ${res.status} ${res.statusText} is not an OK response`)

      return res.text()
    })
    .then(text => {
      console.log('Icon to register is %o', text)
      // TODO
    })
}
