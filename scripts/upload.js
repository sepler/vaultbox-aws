const fs = require('fs');
const axios = require('axios');

let upload_file_name = '';
let api_endpoint = '';

if (fs.existsSync('scripts/config.json')) {
    const script_config = JSON.parse(fs.readFileSync('scripts/config.json'));
    upload_file_name = script_config.upload_file_name;
    api_endpoint = script_config.api_endpoint;
}

(async () => {
    console.log('Reading blob');
    const blob = fs.readFileSync(upload_file_name)
    console.log('Read blob');
    var res = await axios.get(api_endpoint + '/getUploadUrl');
    console.log(res.data);
    const id = res.data.id;
    const uploadUrl = res.data.uploadUrl;
    res = await axios.put(uploadUrl, blob, {
        headers: {
            'Content-Disposition': `attachment; filename="${upload_file_name}"`,
            'Content-Type': 'binary/octet-stream'
        }
    });

    var poll = true;
    while (poll) {
        res = await axios.post(api_endpoint + '/getVaultItem', {id: id})
        console.log(JSON.stringify(res.data));
        if (res.data.status == 'IN_VAULT') {
            break;
        }
        await sleep(1000);
    }
})();

async function sleep(ms) {
    return new Promise(resolve => {
      setTimeout(resolve, ms);
    });
}