package io.devground.dbay.domain.product.category.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import io.devground.dbay.domain.product.category.dto.RegistCategoryRequest;
import io.devground.dbay.domain.product.category.repository.CategoryRepository;
import io.devground.dbay.domain.product.category.service.CategoryService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryInit implements ApplicationRunner {

	private final CategoryService categoryService;
	private final CategoryRepository categoryRepository;
	private final WebRequest webRequest;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		if (categoryRepository.count() == 0) {
			initCategories();
		}
	}

	private void initCategories() {
		// ======================== 1차 카테고리 ========================

		long mobileId = regist(null, "스마트폰/태블릿");
		long laptopId = regist(null, "노트북/PC");
		long audioId = regist(null, "오디오/이어폰");
		long wearableId = regist(null, "웨어러블");
		long cameraId = regist(null, "카메라/캠코더");
		long tvId = regist(null, "TV/모니터");
		long gamingId = regist(null, "게임기기");
		long homeApplianceId = regist(null, "생활가전");
		long kitchenApplianceId = regist(null, "주방가전");
		long beautyApplianceId = regist(null, "미용가전");
		long accessoryId = regist(null, "악세서리/주변기기");
		regist(null, "기타");

		// ======================== 2차 카테고리 ========================

		/**
		 * 스마트폰/태블릿
		 */
		long galaxyId = regist(mobileId, "삼성 갤럭시");
		long appleId = regist(mobileId, "애플 아이폰");
		long tabletId = regist(mobileId, "태블릿");
		long etcMobileId = regist(mobileId, "기타 스마트폰");

		/**
		 * 노트북/PC
		 */
		long gamingLaptopId = regist(laptopId, "게이밍 노트북");
		long ultrabookId = regist(laptopId, "울트라북");
		long macbookId = regist(laptopId, "맥북");
		long desktopId = regist(laptopId, "데스크탑 PC");
		regist(laptopId, "기타");

		/**
		 * 오디오/이어폰
		 */
		long twsId = regist(audioId, "무선 이어폰(TWS)");
		long headphoneId = regist(audioId, "헤드폰");
		long speakerId = regist(audioId, "스피커/사운드바");
		regist(audioId, "기타");

		/**
		 * 웨어러블
		 */
		long smartWatchId = regist(wearableId, "스마트워치");
		long smartBandId = regist(wearableId, "스마트밴드");
		regist(wearableId, "기타");

		/**
		 * 카메라/캠코더
		 */
		regist(cameraId, "DSLR");
		regist(cameraId, "미러리스");
		regist(cameraId, "액션캠");
		regist(cameraId, "드론");
		regist(cameraId, "캠코더");
		regist(cameraId, "즉석카메라");
		regist(cameraId, "기타");

		/**
		 * TV/모니터
		 */
		long smartTvId = regist(tvId, "스마트TV");
		long monitorId = regist(tvId, "모니터");
		long projectorId = regist(tvId, "프로젝터");

		/**
		 * 게임기기
		 */
		regist(gamingId, "플레이스테이션 5");
		regist(gamingId, "플레이스테이션 4");
		regist(gamingId, "Xbox 시리즈");
		regist(gamingId, "닌텐도 스위치");
		regist(gamingId, "게임 핸드헬드");
		regist(gamingId, "기타");

		/**
		 * 생활가전
		 */
		regist(homeApplianceId, "청소기");
		regist(homeApplianceId, "로봇청소기");
		regist(homeApplianceId, "공기청정기");
		regist(homeApplianceId, "제습기/가습기");
		regist(homeApplianceId, "선풍기/서큘레이터");
		regist(homeApplianceId, "히터/온풍기");
		regist(homeApplianceId, "안마의자/기기");
		regist(homeApplianceId, "기타");

		/**
		 * 주방가전
		 */
		regist(kitchenApplianceId, "에어프라이어");
		regist(kitchenApplianceId, "전자레인지");
		regist(kitchenApplianceId, "커피머신");
		regist(kitchenApplianceId, "블렌더/믹서");
		regist(kitchenApplianceId, "전기밥솥");
		regist(kitchenApplianceId, "토스터/샌드위치메이커");
		regist(kitchenApplianceId, "정수기");
		regist(kitchenApplianceId, "기타");

		/**
		 * 미용가전
		 */
		regist(beautyApplianceId, "헤어드라이기");
		regist(beautyApplianceId, "고데기/매직기");
		regist(beautyApplianceId, "전기면도기");
		regist(beautyApplianceId, "제모기");
		regist(beautyApplianceId, "미용기기");
		regist(beautyApplianceId, "기타");

		/**
		 * 악세서리/주변기기
		 */
		long chargerId = regist(accessoryId, "충전기/케이블");
		long caseFilmId = regist(accessoryId, "케이스/보호필름");
		long inputId = regist(accessoryId, "마우스/키보드");
		long storageId = regist(accessoryId, "저장장치");
		regist(accessoryId, "기타");

		// ======================== 3차 카테고리 ========================

		/**
		 * 스마트폰/태블릿
		 * 삼성 갤럭시
		 */
		regist(galaxyId, "갤럭시 S 시리즈");
		regist(galaxyId, "갤럭시 Z 폴드");
		regist(galaxyId, "갤럭시 Z 플립");
		regist(galaxyId, "갤럭시 A 시리즈");
		regist(galaxyId, "갤럭시 노트");
		regist(galaxyId, "기타");

		/**
		 * 스마트폰/태블릿
		 * 애플 아이폰
		 */
		regist(appleId, "아이폰 16 시리즈");
		regist(appleId, "아이폰 15 시리즈");
		regist(appleId, "아이폰 14 시리즈");
		regist(appleId, "아이폰 13 시리즈");
		regist(appleId, "아이폰 12 이하");
		regist(appleId, "아이폰 SE");
		regist(appleId, "기타");

		/**
		 * 스마트폰/태블릿
		 * 태블릿
		 */
		regist(tabletId, "아이패드 프로");
		regist(tabletId, "아이패드 에어");
		regist(tabletId, "아이패드");
		regist(tabletId, "갤럭시 탭 S");
		regist(tabletId, "갤럭시 탭 A");
		regist(tabletId, "기타 태블릿");

		/**
		 * 스마트폰/태블릿
		 * 기타 스마트폰
		 */
		regist(etcMobileId, "구글 픽셀");
		regist(etcMobileId, "샤오미");
		regist(etcMobileId, "LG");
		regist(etcMobileId, "기타");

		/**
		 * 노트북/PC
		 * 게이밍 노트북
		 */
		regist(gamingLaptopId, "RTX 4090 탑재");
		regist(gamingLaptopId, "RTX 4080 탑재");
		regist(gamingLaptopId, "RTX 4070 탑재");
		regist(gamingLaptopId, "RTX 4060 탑재");
		regist(gamingLaptopId, "기타");

		/**
		 * 노트북/PC
		 * 울트라북
		 */
		regist(ultrabookId, "LG 그램");
		regist(ultrabookId, "삼성 갤럭시북");
		regist(ultrabookId, "ASUS 젠북");
		regist(ultrabookId, "기타");

		/**
		 * 노트북/PC
		 * 맥북
		 */
		regist(macbookId, "맥북 프로 (M3)");
		regist(macbookId, "맥북 에어 (M3)");
		regist(macbookId, "맥북 프로 (M2/M1)");
		regist(macbookId, "맥북 에어 (M2/M1)");
		regist(macbookId, "기타");

		/**
		 * 노트북/PC
		 * 데스크탑 PC
		 */
		regist(desktopId, "게이밍 데스크탑");
		regist(desktopId, "사무용 데스크탑");
		regist(desktopId, "맥 미니/스튜디오");
		regist(desktopId, "기타");

		/**
		 * 오디오/이어폰
		 * 무선 이어폰
		 */
		regist(audioId, "에어팟 프로");
		regist(audioId, "에어팟");
		regist(audioId, "갤럭시 버즈");
		regist(audioId, "소니");
		regist(audioId, "보스");
		regist(audioId, "젠하이저");
		regist(audioId, "기타");

		/**
		 * 오디오/이어폰
		 * 헤드폰
		 */
		regist(headphoneId, "노이즈 캔슬링");
		regist(headphoneId, "오버이어");
		regist(headphoneId, "온이어");
		regist(headphoneId, "게이밍 헤드셋");
		regist(headphoneId, "기타");

		/**
		 * 오디오/이어폰
		 * 스피커
		 */
		regist(speakerId, "블루투스 스피커");
		regist(speakerId, "사운드바");
		regist(speakerId, "스마트 스피커");
		regist(speakerId, "기타");

		/**
		 * 웨어러블
		 * 스마트 워치
		 */
		regist(smartWatchId, "애플워치 울트라");
		regist(smartWatchId, "애플워치");
		regist(smartWatchId, "갤럭시워치");
		regist(smartWatchId, "갤럭시워치 클래식");
		regist(smartWatchId, "기타");

		/**
		 * 웨어러블
		 * 스마트 밴드
		 */
		regist(smartBandId, "샤오미 밴드");
		regist(smartBandId, "갤럭시 핏");
		regist(smartBandId, "기타");

		/**
		 * TV/모니터
		 * 스마트TV
		 */
		regist(smartTvId, "OLED TV");
		regist(smartTvId, "QLED TV");
		regist(smartTvId, "4K TV");
		regist(smartTvId, "기타");

		/**
		 * TV/모니터
		 * 모니터
		 */
		regist(monitorId, "게이밍 모니터");
		regist(monitorId, "4K 모니터");
		regist(monitorId, "사무용 모니터");
		regist(monitorId, "기타");

		/**
		 * 악세사리/주변기기
		 * 충전기/케이블
		 */
		regist(chargerId, "무선충전기");
		regist(chargerId, "고속충전기");
		regist(chargerId, "USB 케이블");
		regist(chargerId, "멀티포트 충전기");
		regist(chargerId, "보조배터리");
		regist(chargerId, "기타");

		/**
		 * 악세사리/주변기기
		 * 케이스/보호필름
		 */
		regist(caseFilmId, "스마트폰 케이스");
		regist(caseFilmId, "태블릿 케이스");
		regist(caseFilmId, "노트북 파우치");
		regist(caseFilmId, "보호필름/강화유리");
		regist(caseFilmId, "기타");

		/**
		 * 악세사리/주변기기
		 * 마우스/키보드
		 */
		regist(inputId, "게이밍 마우스");
		regist(inputId, "무선 마우스");
		regist(inputId, "게이밍 키보드");
		regist(inputId, "무선 키보드");
		regist(inputId, "기타");

		/**
		 * 악세사리/주변기기
		 * 저장장치
		 */
		regist(storageId, "외장 SSD");
		regist(storageId, "외장 HDD");
		regist(storageId, "USB 메모리");
		regist(storageId, "메모리 카드");
		regist(storageId, "기타");
	}

	private long regist(Long parentId, String name) {
		RegistCategoryRequest request = new RegistCategoryRequest(name, parentId);
		return categoryService.registCategory(request).id();
	}
}
