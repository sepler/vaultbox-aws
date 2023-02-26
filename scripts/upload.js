const fs = require('fs');
const axios = require('axios');

let upload_file_name = '';
let get_url_endpoint = '';

if (fs.existsSync('scripts/config.json')) {
    const script_config = JSON.parse(fs.readFileSync('scripts/config.json'));
    upload_file_name = script_config.upload_file_name;
    get_url_endpoint = script_config.get_url_endpoint;
}

(async () => {
    console.log('Reading blob');
    const blob = fs.readFileSync(upload_file_name)
    console.log('Read blob');
    var res = await axios.get(get_url_endpoint);
    console.log(res.data);
    const id = res.data.id;
    const uploadUrl = res.data.uploadUrl;
    res = await axios.put(uploadUrl, blob, {
        headers: {
            'Content-Disposition': `attachment; filename="${upload_file_name}"`,
            'Content-Type': 'binary/octet-stream'
        }
    })
})();