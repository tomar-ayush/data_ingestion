"use client"

import { useState } from "react"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"

export default function ClickHouseForm({ onConnect, onPreview, isConnected }) {
  const [config, setConfig] = useState({
    host: "",
    port: "8443",
    database: "default",
    user: "default",
    jwtToken: "",
    table: "",
  })
  const [loading, setLoading] = useState(false)

  const handleChange = (e) => {
    const { name, value } = e.target
    setConfig((prev) => ({ ...prev, [name]: value }))
  }

  const handleConnect = async (e) => {
    e.preventDefault()
    setLoading(true)

    try {
      const success = await onConnect(config)
      if (!success) {
        throw new Error("Connection failed")
      }
    } catch (error) {
      console.error("Connection error:", error)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="space-y-4 border p-4 rounded-md">
      <form onSubmit={handleConnect} className="space-y-3">
        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1">
            <Label htmlFor="host">Host</Label>
            <Input
              id="host"
              name="host"
              value={config.host}
              onChange={handleChange}
              placeholder="e.g., example.clickhouse.cloud"
              required
            />
          </div>

          <div className="space-y-1">
            <Label htmlFor="port">Port</Label>
            <Input
              id="port"
              name="port"
              value={config.port}
              onChange={handleChange}
              placeholder="e.g., 8443"
              required
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-3">
          <div className="space-y-1">
            <Label htmlFor="database">Database</Label>
            <Input
              id="database"
              name="database"
              value={config.database}
              onChange={handleChange}
              placeholder="e.g., default"
              required
            />
          </div>

          <div className="space-y-1">
            <Label htmlFor="user">User</Label>
            <Input
              id="user"
              name="user"
              value={config.user}
              onChange={handleChange}
              placeholder="e.g., default"
              required
            />
          </div>
        </div>

        <div className="space-y-1">
          <Label htmlFor="jwtToken">JWT Token / Password</Label>
          <Input
            id="jwtToken"
            name="jwtToken"
            type="password"
            value={config.jwtToken}
            onChange={handleChange}
            placeholder="Enter your JWT token or password"
            required
          />
        </div>

        <div className="space-y-1">
          <Label htmlFor="table">Table Name</Label>
          <Input
            id="table"
            name="table"
            value={config.table}
            onChange={handleChange}
            placeholder="e.g., my_table"
            required
          />
        </div>

        <div className="flex gap-3 pt-2">
          <Button type="submit" disabled={loading || isConnected}>
            {loading ? "Connecting..." : isConnected ? "Connected" : "Connect"}
          </Button>

          <Button type="button" variant="outline" onClick={onPreview} disabled={!isConnected}>
            Preview Data
          </Button>
        </div>
      </form>
    </div>
  )
}
