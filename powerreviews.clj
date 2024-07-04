(config 
(text-field
:name "clientId"
:label "Client ID"
:placeholder "enter the Client Id")
(password-field
:name "clientSecret" 
:label "Client Secret"
:placeholder "enter Client Secret")

(oauth2/client-credentials
(access-token
(source 
(http/post 
url:"https://enterprise-api.powerreviews.com/oauth2/token")
(query-params 
"grant_type" "client_credentials"
)
(header-params
"Authorization" "Basic BASE64({clientId}:{clientSecret})"
"Content-Type" "application/x-www-form-urlencoded"
))
(fields 
access_token:<="access_token"
expires_in:<="expires_in"
token_type:<="token_type")
)))

(default-source
(base-url "https://enterprise-api.powerreviews.com/v1"
(header-params "Accept" "application/json"))
(auth/oauth2)
(pagination/no-pagination)
(error-handler
(when :status 401 :action fail :message "unauthorized")
))

(temp-entity REVIEW
(api-docs-url "https://apidocs.powerreviews.com/docs/content-api/9cda9169c2819-get-list-of-reviews")
(source 
(http/get :url "/reviews")
(extract-path "reviews")
(setup-test
                  (upon-receiving :code 200 (pass))))
(sync-plan
         (change-capture-cursor 
          (subset/by-time (query-params "created_date" "$SINCE")
                          (format "yyyy-MM-dd'T'HH:mm:ssz")
                          (initial-value  "2021-12-23")
                          )))
(fields
submission_id
id :id))