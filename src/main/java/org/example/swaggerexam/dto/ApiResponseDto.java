package org.example.swaggerexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponseDto<T> {
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponseDto<T> success(String code, T data) {
        return new ApiResponseDto<>(code, null, data);
    }

    public static <T> ApiResponseDto<T> error(String code, String message) {
        return new ApiResponseDto<>(code, message, null);
    }

    /*
      ***** 왜 <T>가 리턴 타입 앞에 필요한가? *****
     제네릭 메서드를 선언하려면 반드시 리턴 타입 앞에 <T>를 추가해야 합니다.

     <T>는 "이 메서드에서 사용할 제네릭 타입 파라미터를 정의한다"는 것을 컴파일러에게 알려줍니다.

     제네릭은 마치 다용도 상자와 같습니다.

     상자(ApiResponse<T>)는 어떤 물건(T)을 담을지 미리 정하지 않고 사용할 때 결정합니다.

      한 번 상자에 물건이 담기면(타입이 결정되면), 다른 종류의 물건은 넣을 수 없습니다.
      *
       // 문자열 상자
       ApiResponse<String> stringBox = ApiResponse.success("CODE", "Hello");

       // 숫자 상자
       ApiResponse<Integer> intBox = ApiResponse.success("CODE", 123);

       // 사용자 객체 상자
       User user = new User(1L, "홍길동");
       ApiResponse<User> userBox = ApiResponse.success("CODE", user);

     */
}

