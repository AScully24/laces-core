# laces-core
Annotation required in project
```
@EntityScan("com.laces")
@EnableJpaRepositories("com.laces")
```

This allows your project to pick up the required entities and repositories of the project.

Required Properties
```
spring:
  mail:
    host: 127.0.0.1
    port: 25
    #username: <login user to smtp server>
    #password: <login password to smtp server>
    properties:
      mail:
        smtp:
          auth: false
          #starttls:
            #enable: true

app:
  url: localhost:8080/
  unvalidatedUserCron: 0 0 1 * * * # 01:00 AM Every Day
  stripe:
    enabled: true
    api-key: pk_test_123
    secret: sk_test_123
    webhook:
      signing-secret: whsec_foo
laces:
  security:
    allowedUrls:
      - rando/

plans:
  subscription-plans:
    -
      stripeId: plan_ID
      overflowStripeId: plan_ID
      recommended: false
      name: Enterprise
      price: £99.00
      features:
        - desc
```
