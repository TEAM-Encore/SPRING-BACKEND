package encore.server.domain.inquiry.converter;

import encore.server.domain.inquiry.dto.CategoryAddRequest;
import encore.server.domain.inquiry.dto.ServiceUseRequest;
import encore.server.domain.inquiry.entity.InquiryAboutCategory;
import encore.server.domain.inquiry.entity.InquiryAboutServiceUse;
import encore.server.domain.user.entity.User;

public class InquiryConverter {
  public static InquiryAboutCategory toInquiryAboutCategory(CategoryAddRequest categoryAddRequest, User user) {
    return InquiryAboutCategory.builder()
        .user(user)
        .reason(categoryAddRequest.reason())
        .requestCategoryName(categoryAddRequest.requestCategoryName())
        .requestPostType(categoryAddRequest.requestPostType())
        .isProcessed(false)
        .build();
  }

  public static InquiryAboutServiceUse toInquiryAboutServiceUse(ServiceUseRequest serviceUseRequest, User user) {
    return InquiryAboutServiceUse.builder()
        .user(user)
        .content(serviceUseRequest.content())
        .emailToReceive(serviceUseRequest.emailToReceive())
        .imageURL(serviceUseRequest.imageUrl())
        .isProcessed(false)
        .build();
  }
}
