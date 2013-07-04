define(['sanity'], function (sanity) {
    describe("The testing environment", function () {
        it("should be sane", function () {
            expect(sanity).toBe(4);
        });
    });
});
