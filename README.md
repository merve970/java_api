# Java API Projesi

Bu proje, kullanıcı yönetimi için bir REST API sağlayan bir Spring Boot uygulamasıdır. JWT (JSON Web Token) ve Basic Auth tabanlı kimlik doğrulama ve yetkilendirme mekanizması kullanır. PostgreSQL veritabanı ile kullanıcı verilerini yönetir.

## Teknolojiler

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Security**: Kimlik doğrulama ve yetkilendirme için.
- **Spring Data JPA**: Veritabanı işlemleri için.
- **PostgreSQL**: Veritabanı olarak.
- **JWT (JSON Web Token)**: Güvenli API erişimi için.
- **Maven**: Proje yönetimi ve bağımlılıklar için.
- **Docker**: Uygulamanın ve veritabanının konteynerize edilmesi için.
- **Lombok**: Kod tekrarını azaltmak için.
- **Springdoc OpenAPI**: API dokümantasyonu için.

## API Endpointleri

### Kimlik Doğrulama

- `POST /auth/login/bearer`: JWT ile giriş yapar ve bir token döner.
- `POST /auth/login/basic`: Basic Auth ile giriş yapar.

### Kullanıcı Yönetimi

- `POST /api/users/register`: Yeni bir kullanıcı oluşturur. (Sadece `ADMIN` ve `MANAGER` rolleri)
- `GET /api/users`: Tüm kullanıcıları listeler. (`ADMIN`, `EMPLOYEE`, `MANAGER` rolleri)
- `PUT /api/users/{id}`: Bir kullanıcının bilgilerini günceller. (Sadece `ADMIN` rolü)
- `PATCH /api/users/{id}/role`: Bir kullanıcının rolünü günceller. (Sadece `ADMIN` rolü)
- `DELETE /api/users/{id}`: Bir kullanıcıyı siler. (Sadece `ADMIN` rolü)

## Projeyi Çalıştırma

### Docker ile

1.  Projeyi klonlayın:
    ```bash
    git clone https://github.com/merve970/java_api.git
    ```
2.  Proje dizinine gidin:
    ```bash
    cd java_api
    ```
3.  Docker Compose ile uygulamayı ve veritabanını başlatın:
    ```bash
    docker-compose up -d
    ```

Uygulama `http://localhost:8080` (JWT) ve `http://localhost:8081` (Basic Auth) adreslerinde çalışacaktır.

### Manuel Olarak

1.  PostgreSQL veritabanını kurun ve çalıştırın.
2.  `src/main/resources/application.yaml` dosyasındaki veritabanı bağlantı bilgilerini kendi yapılandırmanıza göre güncelleyin.
3.  Projeyi Maven ile derleyin ve çalıştırın:
    ```bash
    mvn spring-boot:run
    ```

## Testler

### Örnek Test Senaryosu

- **Senaryo**: Düşük yetkili bir kullanıcının yeni kullanıcı kaydetmeye çalışması.
- **İstek**: `POST /api/users/register`
- **Açıklama**: @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") kısıtlaması gereği EMPLOYEE bu işlemi gerçekleştiremez.
- **Beklenen Sonuç**: <img width="535" height="586" alt="Ekran Resmi 2026-04-29 14 59 27" src="https://github.com/user-attachments/assets/7b43f4f3-7516-4a6c-8015-b23bbe4d6034" />


- **Senaryo**: Bir yöneticinin (Manager) başka bir kullanıcının rolünü değiştirmeye çalışması.
- **İstek**: `PATCH /api/users/{id}/role`
- **Gövde**:
  ```json
  { "role": "ROLE_EMPLOYEE" }
  ```
- **Açıklama**: Rol değiştirme yetkisi kritik bir işlem olduğu için sadece ADMIN rolüne tanımlanmıştır.
- **Beklenen Sonuç**: <img width="534" height="538" alt="Ekran Resmi 2026-04-29 15 04 58" src="https://github.com/user-attachments/assets/730404ab-63c4-40b9-8062-7ba7cab3eb77" />


- **Senaryo**: Yetkili bir personelin yeni bir çalışan kaydetmesi.
- **İstek**: `POST /api/users/register`
- **Gövde**:
  ```json
  {
    "username": "testuser",
    "surname": "testsurname",
    "password": "password",
    "role": "ROLE_EMPLOYEE"
  }
  ```
- **Beklenen Sonuç**:  <img width="376" height="710" alt="Ekran Resmi 2026-04-29 14 09 54" src="https://github.com/user-attachments/assets/bbf86484-6fe2-485e-8f67-30a95163f21b" />
  

- **Senaryo**: Kullanıcı silme işlemi.
- **İstek**: `DELETE /api/users/{id}`
- **Açıklama**: Sistem güvenliği gereği silme yetkisi sadece en üst yetki seviyesindeki ADMIN kullanıcısına verilmiştir.
- **Beklenen Sonuç**: <img width="555" height="715" alt="Ekran Resmi 2026-04-29 14 53 18" src="https://github.com/user-attachments/assets/5d5d016f-da07-4a3c-b419-9aab71f98682" />

---

Bu README dosyası, projenin temel bir anlayışını sağlamak için oluşturulmuştur. Projenin daha detaylı dokümantasyonu için kod içerisindeki yorumları ve Springdoc OpenAPI arayüzünü (`http://localhost:8080/swagger-ui.html`) inceleyebilirsiniz.
