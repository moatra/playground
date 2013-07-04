// Require.js + config
EnvJasmine.loadGlobal(EnvJasmine.testDir + "/require.js");
EnvJasmine.loadGlobal(EnvJasmine.rootDir + "/shim.js");
require.config({
    baseUrl: EnvJasmine.rootDir
});
