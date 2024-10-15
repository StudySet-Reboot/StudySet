
# StudySet 개선 프로젝트 
>

본 프로젝트는 기존 학부 프로젝트인 [StudySet](https://github.com/gagle1231/Study-Set)을 개선하는 것을 목표로 진행되었습니다. 기존 시스템은 해당 전공 과목에서 사용된 특정 기술 스택과 환경을 기반으로 하여 Java JDBC와 JSP로 개발된 MVC 아키텍처를 채택하고 있었습니다. 이번 개선 프로젝트를 통해 이러한 구식 기술을 더 현대적인 기술로 대체하고자 하였으며, 이를 위해 Spring Boot를 활용하여 시스템을 재구성하였습니다.



<br>

## 🔍 Overview 

- **서비스 개선 목표** 🚀
  - **기술 스택 현대화**: 
    - **Spring Data JPA**를 도입하여 데이터베이스 프로그래밍의 유지보수성을 크게 향상시켰습니다.
    - **Spring Security**를 활용하여 보안을 강화했습니다.
    - 이러한 전환을 통해 성능 및 코드의 유지보수성이 크게 개선되었습니다.
    - 또한, 기존 이메일 회원가입 및 로그인을 **소셜 로그인**으로 대체하고, 전체적인 UI를 개선하여 사용자 친화적인 서비스를 제공하고자 했습니다.

  - **개발 및 운영 경험 향상**: 
    - 이전 프로젝트에서는 학교에서 제공한 DB 서버를 사용했으나, 별도의 애플리케이션 서버가 없어 로컬 환경에서만 개발을 진행했습니다.
    - 실무에서는 개발뿐만 아니라 배포와 운영 경험도 중요하다고 판단하여, 이번 프로젝트를 통해 직접 서비스를 배포하고 운영하는 경험을 쌓고자 했습니다.




<br>

## 🧰 Tech Stack 

- **Backend**: Spring Boot, Spring Data JPA, Spring Security, MySQL, Java 17, Docker
- **Frontend**: Thymeleaf, JavaScript, CSS, Ajax
- **배포 환경**
  - AWS EC2
  - Docker 컨테이너

<br>

## Demo

Insert gif or link to demo


## Trouble Shooting