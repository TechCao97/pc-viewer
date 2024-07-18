function getHttpQuery(obj) {
    if (obj == null) {
        return ''
    }
    let result = ''
    Object.keys(obj).forEach(e=>{
        result += e+"="+obj[e]+"&"
    })
    return result.substring(0, result.length - 1)
}

const DEFAULT_STRUCTURE = {
    dataName: 'data',
    codeName: 'errCode',
    codeSuccessValue: 0,
    messageName: 'errMsg'
}

function afterRequest(structure, promise, resolve, reject) {
    promise.then(res => {
        if (res.data) {
            if (res.data[structure.codeName] === structure.codeSuccessValue) {
                resolve(res.data[structure.dataName])
            } else {
                reject({
                    code: res.data[structure.codeName],
                    message: res.data[structure.messageName]
                })
            }
        } else {
            reject({
                code: res.statusCode,
                message: '响应数据为空'
            })
        }
    }).catch(error => {
        reject({
            code: '999',
            message: error.message
        })
    })
}

function createRequest(method, structure, config) {
    let myAxios = axios.create(config)
    return (url, params, options) => {
        return new Promise((resolve, reject) => {
            method = method.toLowerCase()
            let promise
            if (method === 'get') {
                let query = getHttpQuery(params)
                query = query === '' ? query : ('?' + query)
                promise = myAxios.get(url + query, options)
            } else {
                promise = myAxios.post(url, params, options)
            }
            afterRequest(structure, promise, resolve, reject)
        })
    }
}

let getRequest = createRequest('get', DEFAULT_STRUCTURE)

let postRequest = createRequest('post', DEFAULT_STRUCTURE)