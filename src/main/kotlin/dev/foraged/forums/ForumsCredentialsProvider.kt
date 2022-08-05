package dev.foraged.forums

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSCredentialsProvider

object ForumsCredentialsProvider : AWSCredentialsProvider
{
    override fun getCredentials(): AWSCredentials
    {
        return object : AWSCredentials {
            override fun getAWSAccessKeyId(): String {
                return "AKIASMX5JTM6IQCRDNUH"
            }

            override fun getAWSSecretKey(): String {
                return "quw9TGfGHMGWnzxSJQvjlaXsupsyNzfsr2NGarlq"
            }
        }
    }

    override fun refresh()
    {
        TODO("Not yet implemented")
    }
}