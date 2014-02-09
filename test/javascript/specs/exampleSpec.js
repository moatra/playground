define(['loadedSanity'], function (sanity) {
    describe("My sanity", function () {
        it("load from file", function () {
            expect(sanity).to.be.equal(4);
        });

        it("should be postive", function () {
            expect(2+2).to.equal(4);
        });

        it("should fail when I want it", function () {
            expect(3).not.to.equal(4);
        });
    });
});
