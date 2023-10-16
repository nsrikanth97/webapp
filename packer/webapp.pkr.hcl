packer {
  required_plugins {
    amazon = {
      source  = "github.com/hashicorp/amazon"
      version = ">= 1.0.0"
    }
  }
}

variable "aws_region" {
  type    = string
  default = "us-east-1"
}

variable "source_ami" {
  type    = string
  default = "ami-06db4d78cb1d3bbf9" # Ubuntu 22.04 LTS
}

variable "ssh_username" {
  type    = string
  default = "admin"
}

variable "subnet_id" {
  type    = string
  default = "subnet-080382b92edfd20b8"
}
variables {
  access_key        = "AKIA5NJXCNZ4UXG3F3P6"
  access_secret_key = "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"
}`





# https://www.packer.io/plugins/builders/amazon/ebs
source "amazon-ebs" "webapp-ami" {
  access_key = "${var.access_key}"
  secret_key = "${var.access_secret_key}"
  region     = "us-east-1"

  ami_name        = "csye6225_${formatdate("YYYY_MM_DD_hh_mm_ss", timestamp())}"
  ami_description = "AMI for CSYE 6225"
  #  profile = "dev"
  ami_users     = ["362731286542"]
  instance_type = "t2.micro"
  source_ami    = "${var.source_ami}"
  ssh_username  = "${var.ssh_username}"
  subnet_id     = "${var.subnet_id}"


  aws_polling {
    delay_seconds = 30
    max_attempts  = 50
  }

  tags = {
    OS_Version    = "Debian"
    Release       = "Latest"
    Base_AMI_Name = "{{ .SourceAMIName }}"
  }


}


build {
  sources = ["source.amazon-ebs.webapp-ami"]

  provisioner "file" {
    source      = "../target/csye6225-0.0.1-SNAPSHOT.jar"
    destination = "/tmp/csye6225-0.0.1-SNAPSHOT.jar"
  }
  provisioner "file" {
    source      = "../opt/users.csv"
    destination = "/tmp/users.csv"
  }
  provisioner "shell" {
    environment_vars = [
      "DEBIAN_FRONTEND=noninteractive",
      "CHECKPOINT_DISABLE=1"
    ]
    script = "./setup.sh"
  }

}