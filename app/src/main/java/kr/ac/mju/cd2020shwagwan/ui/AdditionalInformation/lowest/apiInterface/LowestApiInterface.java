package kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.apiInterface;

import kr.ac.mju.cd2020shwagwan.ui.AdditionalInformation.lowest.repository.ResponseInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface LowestApiInterface {
    @Headers({"X-Naver-Client-Id:TCRL7dl1UZjhZL_sCypn","X-Naver-Client-Secret:cCWp9kNQoe"})
    @GET("/v1/search/shop.json")
    Call<ResponseInfo> getLowestList(@Query("query") String title,
                             @Query("display") int displaySize,
                             @Query("start") int startPosition,
                             @Query("sort") String sortWay);
}
