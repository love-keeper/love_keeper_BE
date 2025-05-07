terraform {
  backend "s3" {
    bucket         = "lovekeeper-terraform-state"
    key            = "release/terraform.tfstate"
    region         = "ap-northeast-2"
    dynamodb_table = "terraform-state-lock"
    encrypt        = true
  }
}
