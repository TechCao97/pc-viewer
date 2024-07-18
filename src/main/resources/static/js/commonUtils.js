function isNullOrEmpty(obj){
    if(obj == null){
        return true;
    }
    if(typeof obj === "number"){
        return false;
    }
    return !(obj.length > 0 || Object.keys(obj).length > 0);
}

class CountDownLatch {
    constructor(n, fn, args) {
        let queue = []
        for (let i = 0; i < n; i++) {
            queue.push(false)
        }
        this.queue = queue
        this.fn = fn;
        this.args = args
    }

    countDown() {
        let result = true
        let waited = true
        for (let i = 0; i < this.queue.length; i++) {
            if (this.queue[i] === false) {
                if (waited) {
                    this.queue[i] = true
                    waited = false
                } else {
                    result = false
                    break
                }
            }
        }
        if (result) {
            let context = this
            this.fn.apply(context, this.args)
        }
    }
}