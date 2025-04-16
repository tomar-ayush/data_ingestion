"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import DataTransferForm from "@/components/data-transfer-form"
import { useToast } from "@/hooks/use-toast"

export default function Dashboard() {
  const [user, setUser] = useState(null)
  const router = useRouter()
  const { toast } = useToast()

  useEffect(() => {
    // Check if user is logged in
    const userData = localStorage.getItem("user")
    if (!userData) {
      toast({
        title: "Authentication required",
        description: "Please login to access this page",
        variant: "destructive",
      })
      router.push("/")
      return
    }

    setUser(JSON.parse(userData))
  }, [router, toast])

  const handleLogout = () => {
    localStorage.removeItem("user")
    toast({
      title: "Logged out",
      description: "You have been successfully logged out",
    })
    router.push("/")
  }

  if (!user) {
    return <div className="min-h-screen flex items-center justify-center">Loading...</div>
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4 flex justify-between items-center">
          <h1 className="text-xl font-semibold">ClickHouse to Flatfile</h1>
          <div className="flex items-center gap-4">
            <span>Welcome, {user.username}</span>
            <button onClick={handleLogout} className="text-sm text-red-600 hover:text-red-800">
              Logout
            </button>
          </div>
        </div>
      </header>

      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <DataTransferForm />
      </main>
    </div>
  )
}
